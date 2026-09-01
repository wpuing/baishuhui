package com.baishuhui.domain.wallet.service;

import com.baishuhui.user.vo.wallet.WalletChannelDTO;
import com.baishuhui.user.vo.wallet.WalletDTO;

import java.math.BigDecimal;

/**
 * 钱包领域服务：渠道选择与余额判断。
 *
 * @author wei yz
 */
public interface IWalletDsvc {

    /**
     * 选择足以支付 amount 的渠道；优先 preferred。
     *
     * @return 渠道码，不足则 null
     */
    String pickChannel(WalletDTO wallet, String preferred, BigDecimal amount);

    /**
     * 判断渠道余额是否足够。
     */
    boolean enoughBalance(BigDecimal balance, BigDecimal amount);
}
