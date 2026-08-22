package com.baishuhui.supply.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 库存。
 *
 * @author wei yz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseStockDTO {
    private String id;
    private String merchantId;
    private String locationId;
    private String locationName;
    private String category;
    private String unit;
    private BigDecimal quantity;
}
