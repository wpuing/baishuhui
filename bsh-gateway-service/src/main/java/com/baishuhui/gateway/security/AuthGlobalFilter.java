package com.baishuhui.gateway.security;

import com.baishuhui.common.constant.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JWT 鉴权：白名单放行；受保护前缀校验 Bearer Token，并透传用户头。
 * 改密 / 删用户后 Redis cutoff 使未过期 Token 立即失效。
 *
 * @author wei yz
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static final String TOKEN_CUTOFF_PREFIX = "bsh:user:token:cutoff:";

    private final JwtTokenService jwtTokenService;
    private final ObjectProvider<ReactiveStringRedisTemplate> redisProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Value("${bsh.security.enforce:true}")
    private boolean enforce;

    @Value("${bsh.security.whitelist:/api/auth/captcha,/api/auth/login,/api/home/**,/actuator/**}")
    private List<String> whitelist;

    @Value("${bsh.security.protected-prefixes:/api/admin/}")
    private List<String> protectedPrefixes;

    public AuthGlobalFilter(
            JwtTokenService jwtTokenService,
            ObjectProvider<ReactiveStringRedisTemplate> redisProvider) {
        this.jwtTokenService = jwtTokenService;
        this.redisProvider = redisProvider;
    }

    /**
     * 网关统一鉴权入口：可选透传用户头，受保护路径强制校验 Token。
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.debug("auth filter path={}", exchange.getRequest().getURI().getPath());
        // 剥离客户端伪造的身份头，后续仅信任 JWT 解析结果
        ServerWebExchange stripped = stripClientIdentity(exchange);
        final String path = stripped.getRequest().getURI().getPath();
        String auth = stripped.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        // 无 Bearer Token 时按白名单/受保护规则决定是否放行
        if (auth == null || !auth.startsWith("Bearer ")) {
            return continueFilter(stripped, chain, path);
        }
        Claims claims;
        try {
            // 解析 JWT 载荷
            claims = jwtTokenService.parse(auth.substring(7).trim());
        } catch (Exception ex) {
            // Token 无效且路径受保护则直接 401
            if (enforce && !isWhitelisted(path) && isProtected(path)) {
                return unauthorized(stripped, "Token 无效或已过期");
            }
            return continueFilter(stripped, chain, path);
        }
        // 登录/注册等匿名 auth 接口透传 claims 但不强制 cutoff 校验
        if (isAuthAnonymous(path)) {
            return continueWithClaims(stripped, chain, path, claims);
        }
        // 跨模块：Redis 检查改密/删用户后的 Token 失效时间
        return revoked(str(claims.get("uid")), claims.getIssuedAt())
                .flatMap(revoked -> {
                    if (Boolean.TRUE.equals(revoked)) {
                        return unauthorized(stripped, "登录已失效，请重新登录");
                    }
                    return continueWithClaims(stripped, chain, path, claims);
                });
    }

    private ServerWebExchange stripClientIdentity(ServerWebExchange exchange) {
        ServerHttpRequest stripped = exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.remove("X-User-Id");
                    headers.remove("X-Username");
                    headers.remove("X-Roles");
                    headers.remove("X-Perms");
                })
                .build();
        return exchange.mutate().request(stripped).build();
    }

    private Mono<Void> continueWithClaims(
            ServerWebExchange exchange, GatewayFilterChain chain, String path, Claims claims) {
        ServerHttpRequest mutated = exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.set("X-User-Id", str(claims.get("uid")));
                    headers.set("X-Username", firstNonBlank(claims.get("username", String.class), claims.getSubject()));
                    headers.set("X-Roles", joinClaim(claims, "roles", "role"));
                    headers.set("X-Perms", joinClaim(claims, "perms", null));
                })
                .build();
        return continueFilter(exchange.mutate().request(mutated).build(), chain, path);
    }

    private Mono<Void> continueFilter(ServerWebExchange exchange, GatewayFilterChain chain, String path) {
        if (!enforce || isWhitelisted(path) || !isProtected(path)) {
            return chain.filter(exchange);
        }
        // 客户端身份头已剥离，仅认 JWT 写入的 X-User-Id
        String uid = exchange.getRequest().getHeaders().getFirst("X-User-Id");
        if (uid != null && !uid.isBlank()) {
            return chain.filter(exchange);
        }
        return unauthorized(exchange, "缺少 Authorization Bearer Token");
    }

    private Mono<Boolean> revoked(String userId, Date issuedAt) {
        if (userId == null || userId.isBlank()) {
            return Mono.just(false);
        }
        ReactiveStringRedisTemplate redis = redisProvider.getIfAvailable();
        if (redis == null) {
            return Mono.just(false);
        }
        return redis.opsForValue().get(TOKEN_CUTOFF_PREFIX + userId)
                .map(raw -> {
                    try {
                        long cutoff = Long.parseLong(raw.trim());
                        long iat = issuedAt == null ? 0L : issuedAt.toInstant().getEpochSecond();
                        return iat < cutoff;
                    } catch (Exception ex) {
                        return false;
                    }
                })
                .defaultIfEmpty(false)
                .onErrorResume(ex -> {
                    log.warn("token cutoff redis fail userId={}", userId, ex);
                    return Mono.just(false);
                });
    }

    private boolean isAuthAnonymous(String path) {
        return pathMatcher.match("/api/auth/captcha", path)
                || pathMatcher.match("/api/auth/login", path)
                || pathMatcher.match("/api/auth/register", path);
    }

    private boolean isWhitelisted(String path) {
        return whitelist != null && whitelist.stream().anyMatch(p -> pathMatcher.match(p, path));
    }

    private boolean isProtected(String path) {
        return protectedPrefixes != null && protectedPrefixes.stream().anyMatch(path::startsWith);
    }

    private static String str(Object v) {
        return v == null ? "" : String.valueOf(v);
    }

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) {
            return a;
        }
        return b == null ? "" : b;
    }

    private static String joinClaim(Claims claims, String listKey, String singleKey) {
        Object v = claims.get(listKey);
        if (v instanceof List<?> list) {
            return list.stream().map(String::valueOf).collect(Collectors.joining(","));
        }
        if (singleKey != null) {
            Object s = claims.get(singleKey);
            return s == null ? "" : String.valueOf(s);
        }
        return "";
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", ErrorCode.UNAUTHORIZED);
        body.put("message", message);
        body.put("data", null);
        return writeJson(exchange, HttpStatus.UNAUTHORIZED, body);
    }

    private Mono<Void> writeJson(ServerWebExchange exchange, HttpStatus status, Object body) {
        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            bytes = ("{\"code\":\"ERROR\",\"message\":\"" + e.getMessage() + "\"}")
                    .getBytes(StandardCharsets.UTF_8);
        }
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    /**
     * 鉴权过滤器优先于业务路由执行。
     */
    @Override
    public int getOrder() {
        return -100;
    }
}
