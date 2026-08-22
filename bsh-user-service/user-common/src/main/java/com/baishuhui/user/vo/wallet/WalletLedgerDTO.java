package com.baishuhui.user.vo.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包流水。
 *
 * @author wei yz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletLedgerDTO {
    private String id;
    private String channel;
    private String channelLabel;
    private String bizType;
    private String direction;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String orderId;
    private String paymentId;
    private String remark;
    private LocalDateTime createTime;
}
