package com.baishuhui.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Sentinel 网关限流规则骨架（Filter/ExceptionHandler 由自动配置提供）。
 *
 * @author wei yz
 */
@Configuration
public class SentinelGatewayConfig {

    /**
     * 加载默认流控规则，并注册统一限流响应体。
     */
    @PostConstruct
    public void initRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();
        rules.add(new GatewayFlowRule("order-consumer-trade")
                .setCount(100)
                .setIntervalSec(1));
        GatewayRuleManager.loadRules(rules);

        // 限流时返回与业务 Result 结构一致的 JSON，避免前端解析分支
        BlockRequestHandler blockHandler = (ServerWebExchange exchange, Throwable t) -> {
            Map<String, Object> body = new HashMap<>();
            body.put("code", "TOO_MANY_REQUESTS");
            body.put("message", "请求过于频繁，请稍后重试");
            body.put("data", null);
            return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body);
        };
        GatewayCallbackManager.setBlockHandler(blockHandler);
    }
}
