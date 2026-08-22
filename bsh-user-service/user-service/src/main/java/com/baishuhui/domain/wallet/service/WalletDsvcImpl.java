package com.baishuhui.domain.wallet.service;

import com.baishuhui.user.vo.wallet.WalletChannelDTO;
import com.baishuhui.user.vo.wallet.WalletDTO;
import com.baishuhui.common.util.MoneyUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * 钱包领域服务实现。
 *
 * @author wei yz
 */
@Service
public class WalletDsvcImpl implements IWalletDsvc {

    @Override
    public String pickChannel(WalletDTO wallet, String preferred, BigDecimal amount) {
        if (wallet == null || wallet.getChannels() == null || wallet.getChannels().isEmpty()) {
            return null;
        }
        if (StringUtils.hasText(preferred) && enough(wallet, preferred, amount)) {
            return preferred;
        }
        for (WalletChannelDTO channel : wallet.getChannels()) {
            if (channel != null && enoughBalance(channel.getBalance(), amount)) {
                return channel.getChannel();
            }
        }
        return null;
    }

    @Override
    public boolean enoughBalance(BigDecimal balance, BigDecimal amount) {
        return balance != null && MoneyUtil.scale(balance).compareTo(amount) >= 0;
    }

    private boolean enough(WalletDTO wallet, String channel, BigDecimal amount) {
        for (WalletChannelDTO item : wallet.getChannels()) {
            if (item != null && channel.equals(item.getChannel())) {
                return enoughBalance(item.getBalance(), amount);
            }
        }
        return false;
    }
}
