package com.baishuhui.user.vo.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 支付/退款结果。
 *
 * @author wei yz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResultDTO {
    private String paymentId;
    private String userId;
    private String orderId;
    private String channel;
    private BigDecimal amount;
    private String direction;
    private String bizType;
    private String status;
    private BigDecimal balanceAfter;
}
