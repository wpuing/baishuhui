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
 * 钱包退款命令。
 *
 * @author wei yz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletRefundCommand {
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
    private String relatedPaymentId;
    @Size(max = 32)
    private String bizType;
    @Size(max = 256)
    private String remark;
}
