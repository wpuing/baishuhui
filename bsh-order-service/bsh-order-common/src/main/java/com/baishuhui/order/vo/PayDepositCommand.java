package com.baishuhui.order.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 支付定金命令。
 *
 * @author wei yz
 */
@Data
public class PayDepositCommand {

    @NotBlank
    @Size(max = 32)
    private String orderId;

    @NotBlank
    @Size(max = 32)
    private String buyerId;

    /** 支付渠道：SYSTEM / ALIPAY / WECHAT / BANK / CREDIT */
    @NotBlank
    @Size(max = 32)
    private String channel;
}
