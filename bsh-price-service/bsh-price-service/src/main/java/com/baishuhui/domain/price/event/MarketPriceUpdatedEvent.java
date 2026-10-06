package com.baishuhui.domain.price.event;

import com.baishuhui.common.ddd.DomainEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 行情更新领域事件。
 *
 * @author wei yz
 */
public record MarketPriceUpdatedEvent(
        String marketPriceId,
        String sku,
        BigDecimal price,
        LocalDateTime occurredOn
) implements DomainEvent {

    public MarketPriceUpdatedEvent(String marketPriceId, String sku, BigDecimal price) {
        this(marketPriceId, sku, price, LocalDateTime.now());
    }
}
