package com.baishuhui.order.vo;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 完成交易命令。
 *
 * @author wei yz
 */
@Data
public class CompleteTradeCommand {
    @NotBlank
    private String orderId;
    @NotBlank
    @Size(max = 32)
    private String operatorId;
    @NotEmpty(message = "请上传交易结束图片")
    @Size(max = 6, message = "结束图最多 6 张")
    private List<String> completionImageUrls;
    /** 是否售罄；未传则视为未售罄（已完成） */
    private boolean soldOut;
    /** 结单前可改成交额；空则沿用下定金时金额 */
    @DecimalMin(value = "0.01", message = "成交额必须大于 0")
    @Digits(integer = 12, fraction = 2)
    private BigDecimal dealAmount;
}
