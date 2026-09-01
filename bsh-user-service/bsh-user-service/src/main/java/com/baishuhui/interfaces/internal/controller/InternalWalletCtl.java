package com.baishuhui.interfaces.internal.controller;

import com.baishuhui.user.vo.wallet.WalletCreditCommand;
import com.baishuhui.user.vo.wallet.WalletDeductCommand;
import com.baishuhui.user.vo.wallet.WalletRefundCommand;
import com.baishuhui.user.vo.wallet.PaymentResultDTO;
import com.baishuhui.user.vo.wallet.WalletDTO;
import com.baishuhui.common.response.Result;
import com.baishuhui.application.service.wallet.IWalletAsvc;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内部钱包接口：供订单服务扣款 / 退款 / 入账调用。
 *
 * @author wei yz
 */
@Hidden
@RestController
@RequestMapping("/internal/wallet")
@RequiredArgsConstructor
@Slf4j
public class InternalWalletCtl {

    private final IWalletAsvc walletAsvc;

    /**
     * 查询用户钱包总览。
     *
     * @param userId 用户 id
     * @return 钱包总览
     */
    @GetMapping("/{userId}")
    public Result<WalletDTO> getWallet(@PathVariable("userId") String userId) {
        log.info("getWallet invoked");
        return Result.success(walletAsvc.getWallet(userId));
    }

    /**
     * 渠道扣款（定金支付）。
     *
     * @param command 扣款命令
     * @return 支付结果
     */
    @PostMapping("/deduct")
    public Result<PaymentResultDTO> deduct(@Valid @RequestBody WalletDeductCommand command) {
        log.info("deduct invoked");
        return Result.success(walletAsvc.deduct(command));
    }

    /**
     * 渠道退款（取消预定 / 允许取消的已付订单）。
     *
     * @param command 退款命令
     * @return 支付结果
     */
    @PostMapping("/refund")
    public Result<PaymentResultDTO> refund(@Valid @RequestBody WalletRefundCommand command) {
        log.info("refund invoked");
        return Result.success(walletAsvc.refund(command));
    }

    /**
     * 渠道入账（结单定金划转 / 尾款入账卖家）。
     *
     * @param command 入账命令
     * @return 支付结果
     */
    @PostMapping("/credit")
    public Result<PaymentResultDTO> credit(@Valid @RequestBody WalletCreditCommand command) {
        log.info("credit invoked");
        return Result.success(walletAsvc.credit(command));
    }
}
