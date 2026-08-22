package com.baishuhui.gateway.ipaccess;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.gateway.ipaccess.dto.GatewayResult;
import com.baishuhui.gateway.ipaccess.dto.IpRuleSnapshot;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

/**
 * 定时从 user-service 拉取 IP 规则快照。
 *
 * @author wei yz
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IpSnapshotScheduler {

    private final IpAccessProperties properties;

    private final IpAccessGuard ipAccessGuard;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 启动后立即拉一次，之后按配置间隔刷新。
     */
    @Scheduled(initialDelay = 2000, fixedDelay = 15000)
    public void refresh() {
        if (!properties.isEnabled()) {
            return;
        }
        try {
            String json = WebClient.create(properties.getUserServiceBaseUrl())
                    .get()
                    .uri("/internal/ip/snapshot")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(5));
            GatewayResult<IpRuleSnapshot> result = objectMapper.readValue(json,
                    new TypeReference<GatewayResult<IpRuleSnapshot>>() {
                    });
            if (result == null || !ErrorCode.OK.equals(result.getCode()) || result.getData() == null) {
                log.warn("ip snapshot unexpected code={}", result == null ? null : result.getCode());
                return;
            }
            ipAccessGuard.replaceSnapshot(result.getData());
        } catch (Exception ex) {
            log.warn("refresh ip snapshot failed: {}", ex.getMessage());
        }
    }
}
