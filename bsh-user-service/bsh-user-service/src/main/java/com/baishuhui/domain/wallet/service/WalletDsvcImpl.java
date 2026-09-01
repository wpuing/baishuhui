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
        // 空值分支判断
        if (wallet == null || wallet.getChannels() == null || wallet.getChannels().isEmpty()) {
            return null;
        }
        // 字符串非空才继续处理
        if (StringUtils.hasText(preferred) && enough(wallet, preferred, amount)) {
            return preferred;
        }
        // 遍历集合逐项处理
        for (WalletChannelDTO channel : wallet.getChannels()) {
            // 空值分支判断
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
        // 遍历集合逐项处理
        for (WalletChannelDTO item : wallet.getChannels()) {
            // 空值分支判断
            if (item != null && channel.equals(item.getChannel())) {
                return enoughBalance(item.getBalance(), amount);
            }
        }
        return false;
    }
}
