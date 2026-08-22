package com.baishuhui.domain.wallet.entity;

import com.baishuhui.common.persistence.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 支付单：扣款 / 退款 / 测试赠送，以 idempotentKey 唯一防重。
 *
 * @author wei yz
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bsh_payment")
public class PaymentEntity extends BaseEntity {

    /** 用户 id */
    private String userId;

    /** 关联交易订单 id，可空 */
    private String orderId;

    /** 渠道码 */
    private String channel;

    /** 金额（正数） */
    private BigDecimal amount;

    /** 方向：GRANT / DEDUCT / REFUND */
    private String direction;

    /** 业务类型：TEST_GRANT / DEPOSIT_PAY / DEPOSIT_REFUND / ADJUST */
    private String bizType;

    /** 状态：SUCCESS / FAILED */
    private String status;

    /** 幂等键，全局唯一 */
    private String idempotentKey;

    /** 退款关联的原支付单 id */
    private String relatedPaymentId;
}
