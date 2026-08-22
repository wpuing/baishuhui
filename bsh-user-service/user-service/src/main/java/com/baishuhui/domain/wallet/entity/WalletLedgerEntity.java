package com.baishuhui.domain.wallet.entity;

import com.baishuhui.common.persistence.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 钱包流水：每次余额变动一条，含变动前后余额。
 *
 * @author wei yz
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bsh_wallet_ledger")
public class WalletLedgerEntity extends BaseEntity {

    /** 用户 id */
    private String userId;

    /** 渠道码 */
    private String channel;

    /** 业务类型：TEST_GRANT / DEPOSIT_PAY / DEPOSIT_REFUND / ADJUST */
    private String bizType;

    /** 方向：GRANT / DEDUCT / REFUND */
    private String direction;

    /** 变动金额（正数） */
    private BigDecimal amount;

    /** 变动前余额 */
    private BigDecimal balanceBefore;

    /** 变动后余额 */
    private BigDecimal balanceAfter;

    /** 关联交易订单 id，可空 */
    private String orderId;

    /** 关联支付单 id */
    private String paymentId;

    /** 备注 */
    private String remark;
}
