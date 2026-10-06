package com.baishuhui.order.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 品类成交排行 DTO。
 *
 * @author wei yz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRankDTO {
    private String category;
    private long orderCount;
    private BigDecimal totalAmount;
    private int rank;
}
