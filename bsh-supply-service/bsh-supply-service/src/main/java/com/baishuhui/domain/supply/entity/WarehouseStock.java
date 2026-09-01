package com.baishuhui.domain.supply.entity;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.ddd.AggregateRoot;
import com.baishuhui.common.exception.BusinessException;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 仓位上某品类的库存。
 *
 * @author wei yz
 */
@Getter
public class WarehouseStock extends AggregateRoot<String> {

    private String merchantId;
    private String locationId;
    private String category;
    private String unit;
    private BigDecimal quantity;

    protected WarehouseStock() {
    }

    /**
     * 新建零库存记录。
     */
    public static WarehouseStock create(
            String id, String merchantId, String locationId, String category, String unit) {
        // 空值分支判断
        if (merchantId == null || merchantId.isBlank() || locationId == null || locationId.isBlank()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "仓位信息不完整");
        }
        // 空值分支判断
        if (category == null || category.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_SPEC, "品类不能为空");
        }
        WarehouseStock stock = new WarehouseStock();
        stock.setId(id);
        stock.merchantId = merchantId;
        stock.locationId = locationId;
        stock.category = category.trim();
        stock.unit = (unit == null || unit.isBlank()) ? "斤" : unit.trim();
        stock.quantity = BigDecimal.ZERO;
        return stock;
    }

    /**
     * 仓储回放。
     */
    public static WarehouseStock restore(
            String id, String merchantId, String locationId, String category, String unit, BigDecimal quantity) {
        WarehouseStock stock = new WarehouseStock();
        stock.setId(id);
        stock.merchantId = merchantId;
        stock.locationId = locationId;
        stock.category = category;
        stock.unit = unit;
        stock.quantity = quantity == null ? BigDecimal.ZERO : quantity;
        return stock;
    }

    /**
     * 入库。
     */
    public void inbound(BigDecimal qty) {
        assertPositive(qty);
        this.quantity = this.quantity.add(qty);
    }

    /**
     * 出库。
     */
    public void outbound(BigDecimal qty) {
        assertPositive(qty);
        // 业务条件分支
        if (this.quantity.compareTo(qty) < 0) {
            throw new BusinessException(ErrorCode.INVALID_SPEC, "库存不足");
        }
        this.quantity = this.quantity.subtract(qty);
    }

    private static void assertPositive(BigDecimal qty) {
        // 空值分支判断
        if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_SPEC, "数量必须大于 0");
        }
    }
}
