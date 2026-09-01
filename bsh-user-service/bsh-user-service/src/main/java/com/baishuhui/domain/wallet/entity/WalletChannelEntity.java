package com.baishuhui.domain.wallet.entity;

import com.baishuhui.common.persistence.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 钱包渠道余额（user_id + channel 唯一）。
 *
 * @author wei yz
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bsh_wallet_channel")
public class WalletChannelEntity extends BaseEntity {

    /** 用户 id */
    private String userId;

    /** 渠道码：SYSTEM / ALIPAY / WECHAT / BANK / CREDIT */
    private String channel;

    /** 可用余额，单位元，保留两位小数 */
    private BigDecimal balance;

    /** 乐观锁版本号，由 updateBalanceOptimistic 递增 */
    private Integer version;
}
