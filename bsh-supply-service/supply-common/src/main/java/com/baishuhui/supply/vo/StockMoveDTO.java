package com.baishuhui.supply.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 出入库流水。
 *
 * @author wei yz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMoveDTO {
    private String id;
    private String merchantId;
    private String locationId;
    private String locationName;
    private String category;
    private String unit;
    private BigDecimal quantity;
    private String direction;
    private String remark;
    private LocalDateTime createdAt;
}
