package com.baishuhui.gateway.ipaccess;

import com.baishuhui.common.constant.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IP 访问控制：白名单放行、黑名单拒绝、短时海量请求限流并自动拉黑。
 *
 * @author wei yz
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IpAccessGlobalFilter implements GlobalFilter, Ordered {

    private static final String TOO_MANY_REQUESTS = "TOO_MANY_REQUESTS";

    private final IpAccessProperties properties;

    private final ClientIpResolver clientIpResolver;

    private final IpAccessGuard ipAccessGuard;

    private final AutoBanClient autoBanClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 在 JWT 鉴权之前拦截恶意 IP。
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!properties.isEnabled()) {
            return chain.filter(exchange);
        }
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String ip = clientIpResolver.resolve(request, properties.isTrustForwardedHeaders());
        log.debug("ip-access filter ip={} path={}", ip, path);
        if (isSkipped(path)) {
            return chain.filter(exchange);
        }
        if (ipAccessGuard.isWhitelisted(ip)) {
            return chain.filter(exchange);
        }
        if (ipAccessGuard.isBlacklisted(ip)) {
            log.info("blocked blacklisted ip={} path={}", ip, path);
            return reject(exchange, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN, "IP 已被封禁");
        }
        // 验证码 GET 不计入登录爆破桶，避免「刷验证码 → 限流 → 再刷」死循环
        if (isCaptchaPath(path)) {
            return chain.filter(exchange);
        }
        boolean authPath = isLoginPath(path);
        String bucket = authPath ? "auth" : "all";
        int count = ipAccessGuard.increment(ip, bucket, properties.getWindowSeconds());
        int banMax = authPath ? properties.getAuthBanMax() : properties.getBanMax();
        int rateMax = authPath ? properties.getAuthRateMax() : properties.getRateMax();
        if (count >= banMax && shouldAutoBan(ip)) {
            long expire = properties.getBanSeconds() <= 0
                    ? 0L
                    : System.currentTimeMillis() + properties.getBanSeconds() * 1000L;
            ipAccessGuard.banLocal(ip, expire == 0L ? null : expire);
            String reason = authPath ? "登录接口短时高频访问，疑似爆破" : "短时海量请求，自动拉黑";
            autoBanClient.report(ip, count, reason);
            log.warn("auto-ban ip={} path={} count={} window={}s", ip, path, count, properties.getWindowSeconds());
            return reject(exchange, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN, "访问异常，IP 已被封禁");
        }
        if (count >= rateMax) {
            log.info("rate-limited ip={} path={} count={}", ip, path, count);
            return reject(exchange, HttpStatus.TOO_MANY_REQUESTS, TOO_MANY_REQUESTS, "请求过于频繁，请稍后重试");
        }
        return chain.filter(exchange);
    }

    private boolean isSkipped(String path) {
        List<String> skipPaths = properties.getSkipPaths();
        if (skipPaths == null || skipPaths.isEmpty()) {
            return false;
        }
        return skipPaths.stream().anyMatch(p -> pathMatcher.match(p, path));
    }

    private static boolean isLoginPath(String path) {
        return path.startsWith("/api/auth/login");
    }

    private static boolean isCaptchaPath(String path) {
        return path.startsWith("/api/auth/captcha");
    }

    private boolean shouldAutoBan(String ip) {
        if ("unknown".equals(ip)) {
            return false;
        }
        if (properties.isIgnoreLoopback() && isLoopback(ip)) {
            return false;
        }
        if (properties.isAutoBanPublicOnly() && isPrivateOrLocal(ip)) {
            return false;
        }
        return true;
    }

    private static boolean isLoopback(String ip) {
        return "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip);
    }

    private static boolean isPrivateOrLocal(String ip) {
        if (isLoopback(ip)) {
            return true;
        }
        try {
            InetAddress addr = InetAddress.getByName(ip);
            return addr.isSiteLocalAddress() || addr.isAnyLocalAddress() || addr.isLinkLocalAddress();
        } catch (Exception ex) {
            return false;
        }
    }

    private Mono<Void> reject(ServerWebExchange exchange, HttpStatus status, String code, String message) {
        Map<String, Object> body = new HashMap<>(4);
        body.put("code", code);
        body.put("message", message);
        body.put("data", null);
        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            bytes = "{\"code\":\"INTERNAL_ERROR\",\"message\":\"error\",\"data\":null}"
                    .getBytes(StandardCharsets.UTF_8);
        }
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    /**
     * 早于 {@link com.baishuhui.gateway.security.AuthGlobalFilter}（-100）。
     */
    @Override
    public int getOrder() {
        return -200;
    }
}
