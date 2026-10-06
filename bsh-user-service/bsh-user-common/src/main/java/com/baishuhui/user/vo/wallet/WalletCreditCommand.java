package com.baishuhui.user.vo.wallet;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 钱包入账命令（结单划转定金 / 尾款，不作废原扣款单）。
 *
 * @author wei yz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletCreditCommand {
    @NotBlank
    @Size(max = 32)
    private String userId;
    @NotBlank
    @Size(max = 32)
    private String channel;
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;
    @NotBlank
    @Size(max = 32)
    private String orderId;
    @NotBlank
    @Size(max = 64)
    private String idempotentKey;
    @Size(max = 32)
    private String bizType;
    @Size(max = 256)
    private String remark;
}
