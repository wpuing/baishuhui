package com.baishuhui.user.vo.admin;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 管理端向用户渠道调账入参。
 *
 * @author wei yz
 */
@Data
public class WalletAdjustRequest {

    @NotNull(message = "调账金额不能为空")
    @DecimalMin(value = "0.01", message = "调账金额必须大于 0")
    @Digits(integer = 12, fraction = 2)
    private BigDecimal amount;

    /** 渠道，空则系统金额 */
    @Size(max = 32)
    private String channel;

    @Size(max = 128)
    private String remark;
}
