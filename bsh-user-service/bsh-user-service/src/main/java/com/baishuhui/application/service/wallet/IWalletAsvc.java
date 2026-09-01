package com.baishuhui.application.service.wallet;

import com.baishuhui.user.vo.wallet.WalletCreditCommand;
import com.baishuhui.user.vo.wallet.WalletDeductCommand;
import com.baishuhui.user.vo.wallet.WalletRefundCommand;
import com.baishuhui.user.vo.wallet.PaymentResultDTO;
import com.baishuhui.user.vo.wallet.WalletChannelDTO;
import com.baishuhui.user.vo.wallet.WalletDTO;
import com.baishuhui.user.vo.wallet.WalletLedgerDTO;
import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.util.IdUtil;
import com.baishuhui.common.util.MoneyUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应用服务接口（原 IWalletAsvc）。
 *
 * @author wei yz
 */
public interface IWalletAsvc {
    void ensureChannels(String userId);
    void grantTestFunds(String userId);
    PaymentResultDTO testTopUp(String userId, BigDecimal amount, String channel);
    PaymentResultDTO adjust(String userId, BigDecimal amount, String channel, String remark);
    WalletDTO getWallet(String userId);
    List<WalletLedgerDTO> listLedgers(String userId, Integer limit);
    PaymentResultDTO deduct(WalletDeductCommand command);
    PaymentResultDTO credit(WalletCreditCommand command);
    PaymentResultDTO refund(WalletRefundCommand command);
}
