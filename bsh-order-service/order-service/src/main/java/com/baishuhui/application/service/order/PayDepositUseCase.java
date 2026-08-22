package com.baishuhui.application.service.order;

import com.baishuhui.order.vo.PayDepositCommand;
import com.baishuhui.order.vo.TradeOrderDTO;
import com.baishuhui.client.supply.feign.ISupplyFeignService;
import com.baishuhui.supply.vo.LockSupplyCommand;
import com.baishuhui.supply.vo.SupplyInfoDTO;
import com.baishuhui.client.user.feign.IWalletFeignService;
import com.baishuhui.user.vo.wallet.WalletDeductCommand;
import com.baishuhui.user.vo.wallet.WalletRefundCommand;
import com.baishuhui.user.vo.wallet.PaymentResultDTO;
import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.response.Result;
import com.baishuhui.domain.order.entity.TradeOrder;
import com.baishuhui.domain.order.repositories.ITradeOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付定金用例：账户扣款成功后锁定供应并推进订单到已拍下。
 *
 * @author wei yz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayDepositUseCase {
    private static final String BIZ_TYPE_PAY = "DEPOSIT_PAY";
    private static final String BIZ_TYPE_REFUND = "DEPOSIT_REFUND";

    private final ITradeOrderRepository tradeOrderRepository;
    private final ISupplyFeignService supplyFeignClient;
    private final IWalletFeignService walletFeignClient;

    /**
     * 先扣款再锁供应；锁定失败原路退款，避免买家钱货两空。
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<TradeOrderDTO> execute(PayDepositCommand cmd) {
        TradeOrder order = loadPayableOrder(cmd);
        BigDecimal deposit = order.getDepositAmount().amount();

        long start = System.currentTimeMillis();
        Result<PaymentResultDTO> deductRes = walletFeignClient.deduct(WalletDeductCommand.builder()
                .userId(order.getBuyerId())
                .channel(cmd.getChannel())
                .amount(deposit)
                .orderId(order.getId())
                .idempotentKey(BIZ_TYPE_PAY + ":" + order.getId())
                .bizType(BIZ_TYPE_PAY)
                .remark("定金支付 " + order.getOrderNo())
                .build());
        log.info("wallet deduct orderId={} channel={} amount={} cost={}ms",
                order.getId(), cmd.getChannel(), deposit, System.currentTimeMillis() - start);
        if (deductRes == null || !ErrorCode.OK.equals(deductRes.getCode()) || deductRes.getData() == null) {
            throw new BusinessException(deductRes == null ? ErrorCode.PAYMENT_FAILED : deductRes.getCode(),
                    deductRes == null ? "定金扣款失败" : deductRes.getMessage());
        }
        String paymentId = deductRes.getData().getPaymentId();

        // 扣款成功才把供应从预定中推进到已锁定
        Result<SupplyInfoDTO> lockRes = supplyFeignClient.lock(order.getSupplyId(),
                LockSupplyCommand.builder().buyerId(order.getBuyerId()).orderId(order.getId()).build());
        if (lockRes == null || !ErrorCode.OK.equals(lockRes.getCode())) {
            // 供应锁定失败则原渠道退回定金，保持订单仍为待付
            refund(order, cmd.getChannel(), deposit, paymentId);
            throw new BusinessException(lockRes == null ? ErrorCode.LOCK_FAILED : lockRes.getCode(),
                    lockRes == null ? "锁定供应失败已退款" : lockRes.getMessage());
        }

        order.markPaid(cmd.getChannel(), paymentId);
        tradeOrderRepository.save(order);
        log.info("pay deposit ok orderId={} paymentId={} channel={}", order.getId(), paymentId, cmd.getChannel());
        return Result.success(TradeAssembler.toDTO(order));
    }

    /**
     * 校验订单归属、状态与超时。
     */
    private TradeOrder loadPayableOrder(PayDepositCommand cmd) {
        TradeOrder order = tradeOrderRepository.findById(cmd.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND, "订单不存在"));
        if (!cmd.getBuyerId().equals(order.getBuyerId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "仅买家可支付该订单定金");
        }
        if (!TradeOrder.DEPOSIT_PENDING.equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.TRADE_STATUS_INVALID, "仅待付定金订单可支付");
        }
        if (order.depositExpired(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.DEPOSIT_EXPIRED, "定金支付已超时，请重新下定金");
        }
        return order;
    }

    private void refund(TradeOrder order, String channel, BigDecimal amount, String paymentId) {
        Result<PaymentResultDTO> refundRes;
        try {
            refundRes = walletFeignClient.refund(WalletRefundCommand.builder()
                    .userId(order.getBuyerId())
                    .channel(channel)
                    .amount(amount)
                    .orderId(order.getId())
                    .idempotentKey(refundIdempotentKey(order.getId(), paymentId))
                    .relatedPaymentId(paymentId)
                    .bizType(BIZ_TYPE_REFUND)
                    .remark("锁定供应失败退款 " + order.getOrderNo())
                    .build());
        } catch (Exception ex) {
            log.error("refund deposit fail orderId={} paymentId={}", order.getId(), paymentId, ex);
            throw new BusinessException(ErrorCode.PAYMENT_FAILED,
                    "锁定供应失败且退款异常，请联系客服处理 paymentId=" + paymentId);
        }
        if (refundRes == null || !ErrorCode.OK.equals(refundRes.getCode()) || refundRes.getData() == null) {
            log.error("refund deposit biz fail orderId={} paymentId={} code={} msg={}",
                    order.getId(), paymentId,
                    refundRes == null ? null : refundRes.getCode(),
                    refundRes == null ? null : refundRes.getMessage());
            throw new BusinessException(ErrorCode.PAYMENT_FAILED,
                    "锁定供应失败且退款未成功，请联系客服处理 paymentId=" + paymentId);
        }
    }

    /**
     * 退款幂等键须 ≤64（WalletRefundCommand）；用 orderId+paymentId（各 32）保证唯一且合规。
     */
    private static String refundIdempotentKey(String orderId, String paymentId) {
        return orderId + paymentId;
    }
}
