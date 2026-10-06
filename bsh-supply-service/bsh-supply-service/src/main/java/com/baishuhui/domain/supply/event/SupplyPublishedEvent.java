package com.baishuhui.domain.supply.event;

import com.baishuhui.common.ddd.DomainEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 供应发布领域事件。命名：{聚合根}{动作}Event
 *
 * @author wei yz
 */
public record SupplyPublishedEvent(
        String supplyId,
        String merchantId,
        BigDecimal price,
        LocalDateTime occurredOn
) implements DomainEvent {

    public SupplyPublishedEvent(String supplyId, String merchantId, BigDecimal price) {
        this(supplyId, merchantId, price, LocalDateTime.now());
    }
}
