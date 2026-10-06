package com.baishuhui.user.vo.wallet;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 测试期用户自助充值入参。
 *
 * @author wei yz
 */
@Data
public class WalletTestTopUpRequest {

    /** 金额，空则 10000 */
    @DecimalMin(value = "0.01", message = "充值金额必须大于 0")
    @Digits(integer = 12, fraction = 2)
    private BigDecimal amount;

    /** 渠道，空则系统金额 */
    @Size(max = 32)
    private String channel;
}
