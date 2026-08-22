package com.baishuhui.application.service.order;

import com.baishuhui.order.vo.CompleteTradeCommand;
import com.baishuhui.order.vo.TradeOrderDTO;
import com.baishuhui.client.supply.feign.ISupplyFeignService;
import com.baishuhui.supply.vo.CompleteSupplyCommand;
import com.baishuhui.supply.vo.SupplyInfoDTO;
import com.baishuhui.client.user.feign.IWalletFeignService;
import com.baishuhui.user.vo.wallet.WalletCreditCommand;
import com.baishuhui.user.vo.wallet.WalletDeductCommand;
import com.baishuhui.user.vo.wallet.WalletRefundCommand;
import com.baishuhui.user.vo.wallet.PaymentResultDTO;
import com.baishuhui.user.vo.wallet.WalletChannelDTO;
import com.baishuhui.user.vo.wallet.WalletDTO;
import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.response.Result;
import com.baishuhui.common.util.MoneyUtil;
import com.baishuhui.domain.order.entity.TradeOrder;
import com.baishuhui.domain.order.repositories.ITradeOrderRepository;
import com.baishuhui.domain.order.service.IOrderDsvc;
import com.baishuhui.infrastructure.remote.PriceQuoteClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * 完成交易用例：先结算钱包（尾款 + 定金划转），再结供应与订单。
 *
 * @author wei yz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CompleteTradeUseCase {
    private static final String BIZ_DEPOSIT_SETTLE = "DEPOSIT_SETTLE";
    private static final String BIZ_BALANCE_PAY = "BALANCE_PAY";
    private static final String BIZ_BALANCE_SETTLE = "BALANCE_SETTLE";
    private static final String BIZ_BALANCE_REFUND = "BALANCE_REFUND";
    private static final String DEFAULT_CHANNEL = "SYSTEM";

    private final ITradeOrderRepository tradeOrderRepository;
    private final IOrderDsvc orderDsvc;
    private final ISupplyFeignService supplyFeignClient;
    private final IWalletFeignService walletFeignClient;
    private final PriceQuoteClient priceQuoteClient;

    /**
     * 校验操作人后结单（仅已确认 / 进行中）；钱包失败则不结单。
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<TradeOrderDTO> execute(CompleteTradeCommand cmd) {
        TradeOrder order = tradeOrderRepository.findById(cmd.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND, "订单不存在"));

        // 领域校验：当事人 + 可结单状态
        orderDsvc.assertParty(order, cmd.getOperatorId());
        if (orderDsvc.assertCompletableOrDone(order)) {
            return Result.success(TradeAssembler.toDTO(order));
        }

        // 先落成交额再结算：尾款不够时可改金额后重试
        if (cmd.getDealAmount() != null) {
            orderDsvc.reviseDealAmountIfPresent(order, cmd.getDealAmount());
            tradeOrderRepository.save(order);
            log.info("revise dealAmount before complete orderId={} deal={}", order.getId(), cmd.getDealAmount());
        }

        // 先钱包后结单：失败则供应仍锁定，可重试（钱包幂等）
        settleWallet(order);

        Result<SupplyInfoDTO> completeRes = supplyFeignClient.complete(order.getSupplyId(),
                CompleteSupplyCommand.builder()
                        .completionImageUrls(cmd.getCompletionImageUrls())
                        .soldOut(cmd.isSoldOut())
                        .orderId(order.getId())
                        .build());
        if (completeRes == null || !ErrorCode.OK.equals(completeRes.getCode())) {
            throw new BusinessException(completeRes == null ? ErrorCode.COMPLETE_FAILED : completeRes.getCode(),
                    completeRes == null ? "供应结单失败" : completeRes.getMessage());
        }

        orderDsvc.markCompleted(order);
        tradeOrderRepository.save(order);
        order.pullDomainEvents();
        recordDealPrice(order, completeRes.getData());
        return Result.success(TradeAssembler.toDTO(order));
    }

    /**
     * 先结尾款再划定金：避免尾款失败时定金已进卖家。
     */
    private void settleWallet(TradeOrder order) {
        BigDecimal deposit = moneyOrZero(order.getDepositAmount() == null ? null : order.getDepositAmount().amount());
        BigDecimal deal = moneyOrZero(order.getDealAmount() == null ? null : order.getDealAmount().amount());
        BigDecimal tail = deal.subtract(deposit);
        if (tail.compareTo(BigDecimal.ZERO) < 0) {
            tail = BigDecimal.ZERO;
        }
        String settleChannel = StringUtils.hasText(order.getPayChannel()) ? order.getPayChannel() : DEFAULT_CHANNEL;

        if (tail.compareTo(BigDecimal.ZERO) > 0) {
            settleTail(order, tail, settleChannel);
        }
        if (deposit.compareTo(BigDecimal.ZERO) > 0) {
            requireWalletOk(walletFeignClient.credit(WalletCreditCommand.builder()
                    .userId(order.getSellerId())
                    .channel(settleChannel)
                    .amount(deposit)
                    .orderId(order.getId())
                    .idempotentKey(BIZ_DEPOSIT_SETTLE + ":" + order.getId())
                    .bizType(BIZ_DEPOSIT_SETTLE)
                    .remark("结单划转定金 " + order.getOrderNo())
                    .build()), "定金划转卖家失败");
        }
    }

    /**
     * 尾款：优先原定金渠道，不够再选有余额渠道；扣款成功后入账卖家。
     */
    private void settleTail(TradeOrder order, BigDecimal tail, String preferredChannel) {
        Result<WalletDTO> walletRes = walletFeignClient.getWallet(order.getBuyerId());
        if (walletRes == null || !ErrorCode.OK.equals(walletRes.getCode()) || walletRes.getData() == null) {
            throw new BusinessException(ErrorCode.WALLET_NOT_FOUND, "无法读取买家钱包，尾款未结算");
        }
        String payChannel = pickChannel(walletRes.getData(), preferredChannel, tail);
        if (payChannel == null) {
            throw new BusinessException(ErrorCode.WALLET_INSUFFICIENT,
                    "尾款不足（¥" + MoneyUtil.scale(tail) + "）。可降低成交额后重试，"
                            + "或让买家到「我的账户」测试充值，也可请管理员在用户审核里调账");
        }
        PaymentResultDTO deduct = requireWalletOk(walletFeignClient.deduct(WalletDeductCommand.builder()
                .userId(order.getBuyerId())
                .channel(payChannel)
                .amount(tail)
                .orderId(order.getId())
                .idempotentKey(BIZ_BALANCE_PAY + ":" + order.getId())
                .bizType(BIZ_BALANCE_PAY)
                .remark("结单支付尾款 " + order.getOrderNo())
                .build()), "尾款扣款失败");
        try {
            requireWalletOk(walletFeignClient.credit(WalletCreditCommand.builder()
                    .userId(order.getSellerId())
                    .channel(payChannel)
                    .amount(tail)
                    .orderId(order.getId())
                    .idempotentKey(BIZ_BALANCE_SETTLE + ":" + order.getId())
                    .bizType(BIZ_BALANCE_SETTLE)
                    .remark("结单尾款入账 " + order.getOrderNo())
                    .build()), "尾款入账卖家失败");
        } catch (RuntimeException ex) {
            refundTail(order, payChannel, tail, deduct.getPaymentId());
            throw ex;
        }
    }

    private void refundTail(TradeOrder order, String channel, BigDecimal amount, String paymentId) {
        Result<PaymentResultDTO> refundRes;
        try {
            refundRes = walletFeignClient.refund(WalletRefundCommand.builder()
                    .userId(order.getBuyerId())
                    .channel(channel)
                    .amount(amount)
                    .orderId(order.getId())
                    .idempotentKey(BIZ_BALANCE_REFUND + ":" + order.getId())
                    .relatedPaymentId(paymentId)
                    .bizType(BIZ_BALANCE_REFUND)
                    .remark("尾款入账失败退回 " + order.getOrderNo())
                    .build());
        } catch (Exception ex) {
            log.error("tail refund exception orderId={} paymentId={}", order.getId(), paymentId, ex);
            throw new BusinessException(ErrorCode.PAYMENT_FAILED,
                    "尾款入账失败且退款异常，请联系客服处理 paymentId=" + paymentId);
        }
        if (refundRes == null || !ErrorCode.OK.equals(refundRes.getCode())) {
            log.error("tail refund biz fail orderId={} paymentId={}", order.getId(), paymentId);
            throw new BusinessException(ErrorCode.PAYMENT_FAILED,
                    "尾款入账失败且退款未成功，请联系客服处理 paymentId=" + paymentId);
        }
    }

    private String pickChannel(WalletDTO wallet, String preferred, BigDecimal amount) {
        if (wallet.getChannels() == null || wallet.getChannels().isEmpty()) {
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

    private boolean enough(WalletDTO wallet, String channel, BigDecimal amount) {
        for (WalletChannelDTO item : wallet.getChannels()) {
            if (item != null && channel.equals(item.getChannel())) {
                return enoughBalance(item.getBalance(), amount);
            }
        }
        return false;
    }

    private boolean enoughBalance(BigDecimal balance, BigDecimal amount) {
        return balance != null && MoneyUtil.scale(balance).compareTo(amount) >= 0;
    }

    private PaymentResultDTO requireWalletOk(Result<PaymentResultDTO> result, String fallback) {
        if (result == null || !ErrorCode.OK.equals(result.getCode()) || result.getData() == null) {
            throw new BusinessException(result == null ? ErrorCode.PAYMENT_FAILED : result.getCode(),
                    result == null ? fallback : result.getMessage());
        }
        return result.getData();
    }

    private BigDecimal moneyOrZero(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : MoneyUtil.scale(amount);
    }

    private void recordDealPrice(TradeOrder order, SupplyInfoDTO supply) {
        String sku = supply != null && StringUtils.hasText(supply.getCategory())
                ? supply.getCategory() : order.getCategory();
        BigDecimal price = supply != null ? supply.getPrice() : null;
        String unit = supply != null ? supply.getUnit() : null;
        priceQuoteClient.recordDeal(sku, price, unit);
    }
}
