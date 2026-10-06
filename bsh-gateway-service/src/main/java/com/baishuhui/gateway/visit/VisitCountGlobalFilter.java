package com.baishuhui.gateway.visit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 已废弃：今日访问改为登录去重，不再按请求 PV 上报。默认关闭。
 *
 * @author wei yz
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VisitCountGlobalFilter implements GlobalFilter, Ordered {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final Executor visitExecutor = new ThreadPoolExecutor(
            1,
            4,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(200),
            r -> {
                Thread t = new Thread(r, "gw-visit");
                t.setDaemon(true);
                return t;
            },
            new ThreadPoolExecutor.DiscardPolicy());

    @Value("${bsh.gateway.ip-access.user-service-base-url:http://127.0.0.1:8081}")
    private String userServiceBaseUrl;

    @Value("${bsh.gateway.visit-count.enabled:false}")
    private boolean enabled;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (enabled && shouldCount(exchange.getRequest().getURI().getPath())) {
            visitExecutor.execute(this::report);
        }
        return chain.filter(exchange);
    }

    private boolean shouldCount(String path) {
        if (path == null) {
            return false;
        }
        if (pathMatcher.match("/actuator/**", path)
                || pathMatcher.match("/ws/**", path)
                || pathMatcher.match("/doc.html", path)
                || pathMatcher.match("/v3/api-docs/**", path)
                || pathMatcher.match("/swagger-ui/**", path)
                || pathMatcher.match("/internal/**", path)
                || pathMatcher.match("/api/auth/captcha", path)) {
            return false;
        }
        return path.startsWith("/api/") || path.startsWith("/uploads/");
    }

    private void report() {
        try {
            WebClient.create(userServiceBaseUrl)
                    .post()
                    .uri("/internal/user/stats/visit")
                    .retrieve()
                    .toBodilessEntity()
                    .block(Duration.ofSeconds(3));
        } catch (Exception ex) {
            log.debug("visit count report fail: {}", ex.getMessage());
        }
    }

    @Override
    public int getOrder() {
        return -50;
    }
}
