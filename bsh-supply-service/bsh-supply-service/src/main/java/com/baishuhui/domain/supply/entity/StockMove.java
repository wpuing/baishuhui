package com.baishuhui.domain.supply.entity;

import com.baishuhui.supply.constant.StockMoveTypeConstants;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 出入库流水（非聚合，由仓储保存）。
 *
 * @author wei yz
 */
@Getter
public class StockMove {

    public static final String IN = StockMoveTypeConstants.IN;
    public static final String OUT = StockMoveTypeConstants.OUT;

    private final String id;
    private final String merchantId;
    private final String locationId;
    private final String category;
    private final String unit;
    private final BigDecimal quantity;
    private final String direction;
    private final String remark;
    private final LocalDateTime createdAt;

    /**
     * 构造流水。
     */
    public StockMove(
            String id,
            String merchantId,
            String locationId,
            String category,
            String unit,
            BigDecimal quantity,
            String direction,
            String remark,
            LocalDateTime createdAt) {
        this.id = id;
        this.merchantId = merchantId;
        this.locationId = locationId;
        this.category = category;
        this.unit = unit;
        this.quantity = quantity;
        this.direction = direction;
        this.remark = remark;
        this.createdAt = createdAt;
    }
}
