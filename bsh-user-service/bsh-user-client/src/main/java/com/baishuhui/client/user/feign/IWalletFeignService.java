package com.baishuhui.client.user.feign;

import com.baishuhui.user.vo.wallet.WalletCreditCommand;
import com.baishuhui.user.vo.wallet.WalletDeductCommand;
import com.baishuhui.user.vo.wallet.WalletRefundCommand;
import com.baishuhui.user.vo.wallet.PaymentResultDTO;
import com.baishuhui.user.vo.wallet.WalletDTO;
import com.baishuhui.common.response.Result;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 用户钱包内部 Feign。
 *
 * @author wei yz
 */
@FeignClient(name = "bsh-user-service", contextId = "walletFeignClient", url = "${bsh.services.user:}")
public interface IWalletFeignService {

    @GetMapping("/internal/wallet/{userId}")
    Result<WalletDTO> getWallet(@PathVariable("userId") String userId);

    @PostMapping("/internal/wallet/deduct")
    Result<PaymentResultDTO> deduct(@Valid @RequestBody WalletDeductCommand command);

    @PostMapping("/internal/wallet/refund")
    Result<PaymentResultDTO> refund(@Valid @RequestBody WalletRefundCommand command);

    @PostMapping("/internal/wallet/credit")
    Result<PaymentResultDTO> credit(@Valid @RequestBody WalletCreditCommand command);
}
