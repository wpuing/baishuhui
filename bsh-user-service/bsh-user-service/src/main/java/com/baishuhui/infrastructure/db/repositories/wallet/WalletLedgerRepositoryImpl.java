package com.baishuhui.infrastructure.db.repositories.wallet;

import com.baishuhui.domain.wallet.entity.WalletLedgerEntity;
import com.baishuhui.domain.wallet.repositories.IWalletLedgerRepository;
import com.baishuhui.infrastructure.db.mapper.wallet.WalletLedgerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * 钱包流水仓储实现。
 *
 * @author wei yz
 */
@Repository
@RequiredArgsConstructor
public class WalletLedgerRepositoryImpl implements IWalletLedgerRepository {

    private final WalletLedgerMapper walletLedgerMapper;

    @Override
    public long countByUserAndBizType(String userId, String bizType) {
        return walletLedgerMapper.countByUserAndBizType(userId, bizType);
    }

    @Override
    public List<WalletLedgerEntity> listRecentByUser(String userId, int limit) {
        List<WalletLedgerEntity> list = walletLedgerMapper.selectRecentByUser(userId, limit);
        return list == null ? Collections.emptyList() : list;
    }

    @Override
    public void insert(WalletLedgerEntity entity) {
        walletLedgerMapper.insert(entity);
    }
}
