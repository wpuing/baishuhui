package com.baishuhui.gateway.ipaccess;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 异步通知 user-service 落库自动黑名单。
 *
 * @author wei yz
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AutoBanClient {

    private final IpAccessProperties properties;

    private final Executor ipAccessExecutor = new ThreadPoolExecutor(
            2,
            8,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            r -> {
                Thread t = new Thread(r, "gw-ip-access");
                t.setDaemon(true);
                return t;
            },
            new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 后台线程上报自动拉黑，不阻塞网关事件循环。
     *
     * @param ip 客户端 IP
     * @param hitCount 窗口内次数
     * @param reason 原因
     */
    public void report(String ip, int hitCount, String reason) {
        ipAccessExecutor.execute(() -> persist(ip, hitCount, reason));
    }

    private void persist(String ip, int hitCount, String reason) {
        try {
            Map<String, Object> body = new HashMap<>(4);
            body.put("ip", ip);
            body.put("hitCount", hitCount);
            body.put("reason", reason);
            int banSeconds = properties.getBanSeconds();
            body.put("expireMinutes", banSeconds <= 0 ? 0 : Math.max(1, banSeconds / 60));
            WebClient.create(properties.getUserServiceBaseUrl())
                    .post()
                    .uri("/internal/ip/auto-ban")
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block(Duration.ofSeconds(5));
            log.warn("auto-ban persisted ip={} hits={}", ip, hitCount);
        } catch (Exception ex) {
            log.warn("auto-ban persist failed ip={} msg={}", ip, ex.getMessage());
        }
    }
}
