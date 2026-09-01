package com.baishuhui.price.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 行情报价出参。
 *
 * @author wei yz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceQuoteDTO {
    private String sku;
    private BigDecimal price;
    private String unit;
    private LocalDateTime snapshotTime;
    private String payload;
}
