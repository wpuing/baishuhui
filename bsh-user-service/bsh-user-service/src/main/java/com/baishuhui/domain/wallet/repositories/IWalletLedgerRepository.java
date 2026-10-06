package com.baishuhui.domain.wallet.repositories;

import com.baishuhui.domain.wallet.entity.WalletLedgerEntity;

import java.util.List;

/**
 * 钱包流水仓储。
 *
 * @author wei yz
 */
public interface IWalletLedgerRepository {

    /**
     * 用户某业务类型流水条数。
     */
    long countByUserAndBizType(String userId, String bizType);

    /**
     * 用户最近流水。
     */
    List<WalletLedgerEntity> listRecentByUser(String userId, int limit);

    /**
     * 写入流水。
     */
    void insert(WalletLedgerEntity entity);
}
