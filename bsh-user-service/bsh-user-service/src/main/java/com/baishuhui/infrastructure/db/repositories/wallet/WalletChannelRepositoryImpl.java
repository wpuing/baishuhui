package com.baishuhui.infrastructure.db.repositories.wallet;

import com.baishuhui.domain.wallet.entity.WalletChannelEntity;
import com.baishuhui.domain.wallet.repositories.IWalletChannelRepository;
import com.baishuhui.infrastructure.db.mapper.wallet.WalletChannelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * 钱包渠道仓储实现。
 *
 * @author wei yz
 */
@Repository
@RequiredArgsConstructor
public class WalletChannelRepositoryImpl implements IWalletChannelRepository {

    private final WalletChannelMapper walletChannelMapper;

    @Override
    public WalletChannelEntity findByUserAndChannel(String userId, String channel) {
        return walletChannelMapper.selectByUserAndChannel(userId, channel);
    }

    @Override
    public List<WalletChannelEntity> listByUser(String userId) {
        List<WalletChannelEntity> list = walletChannelMapper.selectByUser(userId);
        return list == null ? Collections.emptyList() : list;
    }

    @Override
    public void insert(WalletChannelEntity entity) {
        walletChannelMapper.insert(entity);
    }

    @Override
    public int updateBalanceOptimistic(String id, BigDecimal balance, Integer version) {
        return walletChannelMapper.updateBalanceOptimistic(id, balance, version);
    }
}
