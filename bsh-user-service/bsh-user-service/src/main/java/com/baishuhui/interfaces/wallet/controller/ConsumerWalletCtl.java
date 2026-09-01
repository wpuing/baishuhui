package com.baishuhui.interfaces.wallet.controller;

import com.baishuhui.user.vo.wallet.PaymentResultDTO;
import com.baishuhui.user.vo.wallet.WalletDTO;
import com.baishuhui.user.vo.wallet.WalletLedgerDTO;
import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.response.Result;
import com.baishuhui.application.service.wallet.IWalletAsvc;
import com.baishuhui.infrastructure.security.AuthUserPrincipal;
import com.baishuhui.user.vo.wallet.WalletTestTopUpRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户前台「我的账户」：查看多渠道余额与流水。
 *
 * @author wei yz
 */
@Tag(name = "用户前台-我的账户")
@RestController
@RequestMapping("/api/consumer/wallet")
@RequiredArgsConstructor
@Slf4j
public class ConsumerWalletCtl {

    private final IWalletAsvc walletAsvc;

    /**
     * 当前登录用户钱包总览。
     *
     * @return 各渠道余额与合计
     */
    @Operation(summary = "我的账户余额")
    @GetMapping
    public Result<WalletDTO> mine() {
        log.info("mine invoked");
        return Result.success(walletAsvc.getWallet(currentUserId()));
    }

    /**
     * 当前登录用户最近流水。
     *
     * @param limit 条数，默认 20，最大 100
     * @return 流水列表
     */
    @Operation(summary = "我的账户流水")
    @GetMapping("/ledgers")
    public Result<List<WalletLedgerDTO>> ledgers(@RequestParam(required = false) Integer limit) {
        log.info("ledgers invoked");
        return Result.success(walletAsvc.listLedgers(currentUserId(), limit));
    }

    /**
     * 测试期自助充值，便于结单尾款不足时补余额。
     *
     * @param request 金额与渠道，均可空
     * @return 入账结果
     */
    @Operation(summary = "测试充值")
    @PostMapping("/test-topup")
    public Result<PaymentResultDTO> testTopUp(@Valid @RequestBody(required = false) WalletTestTopUpRequest request) {
        log.info("testTopUp invoked");
        WalletTestTopUpRequest body = request == null ? new WalletTestTopUpRequest() : request;
        return Result.success(walletAsvc.testTopUp(currentUserId(), body.getAmount(), body.getChannel()));
    }

    private String currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthUserPrincipal principal)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录");
        }
        return principal.getId();
    }
}
