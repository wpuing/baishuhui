package com.baishuhui.domain.price.entity;

import com.baishuhui.common.ddd.AggregateRoot;
import com.baishuhui.domain.price.event.MarketPriceUpdatedEvent;
import com.baishuhui.domain.price.entity.vo.PriceSnapshot;
import com.baishuhui.domain.price.entity.vo.Unit;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 行情聚合根（示例骨架）。
 *
 * @author wei yz
 */
@Getter
public class MarketPrice extends AggregateRoot<String> {

    private String sku;
    private PriceSnapshot latest;
    private Unit unit;

    protected MarketPrice() {
    }

    private MarketPrice(String id, String sku, Unit unit, PriceSnapshot latest) {
        super(id);
        this.sku = sku;
        this.unit = unit;
        this.latest = latest;
    }

    public static MarketPrice create(String id, String sku, Unit unit, BigDecimal price) {
        PriceSnapshot snapshot = new PriceSnapshot(price, unit, null);
        return new MarketPrice(id, sku, unit, snapshot);
    }

    /**
     * 更新行情（领域规则示例）。
     */
    public void updatePrice(BigDecimal newPrice) {
        this.latest = new PriceSnapshot(newPrice, this.unit, null);
        registerEvent(new MarketPriceUpdatedEvent(getId(), sku, newPrice));
    }
}
