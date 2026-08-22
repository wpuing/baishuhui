package com.baishuhui.domain.supply.event;

import com.baishuhui.common.ddd.DomainEvent;

import java.time.LocalDateTime;

/**
 * 供应完成领域事件。
 *
 * @author wei yz
 */
public record SupplyCompletedEvent(
        String supplyId,
        String buyerId,
        String orderId,
        String finalStatus,
        LocalDateTime occurredOn
) implements DomainEvent {

    public SupplyCompletedEvent(String supplyId, String buyerId, String orderId, String finalStatus) {
        this(supplyId, buyerId, orderId, finalStatus, LocalDateTime.now());
    }
}
