package com.baishuhui.domain.order.event;

import com.baishuhui.common.ddd.DomainEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易订单完成领域事件。
 *
 * @author wei yz
 */
public record TradeOrderCompletedEvent(
        String orderId,
        String orderNo,
        String supplyId,
        String category,
        BigDecimal dealAmount,
        LocalDateTime occurredOn
) implements DomainEvent {

    public TradeOrderCompletedEvent(String orderId, String orderNo, String supplyId, String category, BigDecimal dealAmount) {
        this(orderId, orderNo, supplyId, category, dealAmount, LocalDateTime.now());
    }
}
