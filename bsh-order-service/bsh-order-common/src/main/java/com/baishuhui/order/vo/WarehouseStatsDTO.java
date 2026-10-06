package com.baishuhui.order.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 商家仓库统计（已完成交易）。
 *
 * @author wei yz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseStatsDTO {

    private long totalCount;

    private BigDecimal totalAmount;

    /** 当日（按完成时间，无则创建时间）成交总额 */
    private BigDecimal dayAmount;

    /** 当月成交总额 */
    private BigDecimal monthAmount;

    /** 当年成交总额 */
    private BigDecimal yearAmount;

    @Builder.Default
    private List<NamedAmountDTO> byCategory = new ArrayList<>();

    @Builder.Default
    private List<NamedAmountDTO> byMonth = new ArrayList<>();

    /**
     * 分组金额项。
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NamedAmountDTO {
        private String name;
        private long count;
        private BigDecimal amount;
    }
}
