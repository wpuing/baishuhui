package com.baishuhui.domain.supply.event;

import com.baishuhui.common.ddd.DomainEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 供应锁定领域事件。
 *
 * @author wei yz
 */
public record SupplyLockedEvent(
        String supplyId,
        String buyerId,
        String orderId,
        BigDecimal depositAmount,
        LocalDateTime occurredOn
) implements DomainEvent {

    public SupplyLockedEvent(String supplyId, String buyerId, String orderId, BigDecimal depositAmount) {
        this(supplyId, buyerId, orderId, depositAmount, LocalDateTime.now());
    }
}
