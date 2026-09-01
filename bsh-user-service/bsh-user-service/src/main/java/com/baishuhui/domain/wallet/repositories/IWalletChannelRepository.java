package com.baishuhui.domain.wallet.repositories;

import com.baishuhui.domain.wallet.entity.WalletChannelEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * 钱包渠道余额仓储。
 *
 * @author wei yz
 */
public interface IWalletChannelRepository {

    /**
     * 按用户与渠道查询。
     */
    WalletChannelEntity findByUserAndChannel(String userId, String channel);

    /**
     * 用户全部渠道。
     */
    List<WalletChannelEntity> listByUser(String userId);

    /**
     * 新增渠道行。
     */
    void insert(WalletChannelEntity entity);

    /**
     * 乐观锁更新余额。
     *
     * @return 影响行数
     */
    int updateBalanceOptimistic(String id, BigDecimal balance, Integer version);
}
