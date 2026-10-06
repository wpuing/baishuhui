package com.baishuhui.domain.order.event;

import com.baishuhui.common.ddd.DomainEvent;

import java.time.LocalDateTime;

/**
 * 交易订单创建领域事件。
 *
 * @author wei yz
 */
public record TradeOrderCreatedEvent(
        String orderId,
        String orderNo,
        String supplyId,
        String buyerId,
        String sellerId,
        LocalDateTime occurredOn
) implements DomainEvent {

    public TradeOrderCreatedEvent(String orderId, String orderNo, String supplyId, String buyerId, String sellerId) {
        this(orderId, orderNo, supplyId, buyerId, sellerId, LocalDateTime.now());
    }
}
