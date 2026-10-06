package com.baishuhui.interfaces.order.schedule;

import com.baishuhui.application.service.order.IOrderAsvc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 待付定金超时释放调度。
 *
 * @author wei yz
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DepositExpireScheduler {
    private final IOrderAsvc orderAsvc;

    /**
     * 每分钟扫描超时待付订单，取消订单并释放供应。
     */
    @Scheduled(fixedDelay = 60_000L, initialDelay = 30_000L)
    public void cancelExpiredDeposits() {
        try {
            int cancelled = orderAsvc.cancelExpiredDeposits();
            if (cancelled > 0) {
                log.info("deposit expire scan cancelled={}", cancelled);
            }
        } catch (Exception ex) {
            log.error("deposit expire scan fail", ex);
        }
    }
}
