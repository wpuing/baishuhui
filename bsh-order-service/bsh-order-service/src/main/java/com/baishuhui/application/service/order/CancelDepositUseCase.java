package com.baishuhui.application.service.order;

import com.baishuhui.order.vo.CancelDepositCommand;
import com.baishuhui.order.vo.TradeActionCommand;
import com.baishuhui.order.vo.TradeOrderDTO;
import com.baishuhui.client.supply.feign.ISupplyFeignService;
import com.baishuhui.supply.vo.UnlockSupplyCommand;
import com.baishuhui.client.user.feign.IWalletFeignService;
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
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 取消定金订单用例：未付直接释放，已付先退款再释放。
 *
 * @author wei yz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CancelDepositUseCase {
    private static final String BIZ_TYPE_REFUND = "DEPOSIT_REFUND";

    private final ITradeOrderRepository tradeOrderRepository;
    private final ISupplyFeignService supplyFeignClient;
    private final IWalletFeignService walletFeignClient;
    private final TradeNotifyHelper tradeNotifyHelper;

    /**
     * 按订单状态分流：待付仅释放供应，已付先原渠道退款再释放。
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<TradeOrderDTO> execute(CancelDepositCommand cmd) {
        TradeOrder order = tradeOrderRepository.findById(cmd.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND, "订单不存在"));
        // 字段相等性校验
        if (!cmd.getBuyerId().equals(order.getBuyerId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "仅买家可取消该订单");
        }

        String status = TradeOrder.normalizeStatus(order.getStatus());
        // 字段相等性校验
        if (TradeOrder.DEPOSIT_PENDING.equals(status)) {
            releaseSupply(order);
            order.cancelPending();
        } else if (TradeOrder.PLACED.equals(status) || TradeOrder.PENDING_CONFIRM.equals(status)) {
            refundDeposit(order);
            releaseSupply(order);
            order.cancelPaid();
        } else {
            throw new BusinessException(ErrorCode.TRADE_STATUS_INVALID, "当前订单状态不可取消");
        }

        tradeOrderRepository.save(order);
        tradeNotifyHelper.depositCancelled(order, "订单已取消");
        log.info("cancel deposit ok orderId={} fromStatus={} buyerId={}", order.getId(), status, cmd.getBuyerId());
        return Result.success(TradeAssembler.toDTO(order));
    }

    /**
     * 买卖双方取消：待付仅买家；已拍下 / 待确认双方均可（退定金并释放供应）。
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<TradeOrderDTO> executeByOperator(TradeActionCommand cmd) {
        TradeOrder order = tradeOrderRepository.findById(cmd.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND, "订单不存在"));
        boolean buyer = cmd.getOperatorId().equals(order.getBuyerId());
        boolean seller = cmd.getOperatorId().equals(order.getSellerId());
        // 操作人必须是买家或卖家之一
        if (!buyer && !seller) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "仅供需双方可取消");
        }
        String status = TradeOrder.normalizeStatus(order.getStatus());
        // 待付定金：仅买家可取消
        if (TradeOrder.DEPOSIT_PENDING.equals(status)) {
            // 卖家在待付阶段不可取消
            if (seller) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "待付定金阶段卖家仅可查看");
            }
            releaseSupply(order);
            order.cancelPending();
        } else if (TradeOrder.PLACED.equals(status) || TradeOrder.PENDING_CONFIRM.equals(status)) {
            refundDeposit(order);
            releaseSupply(order);
            order.cancelPaid();
        } else {
            throw new BusinessException(ErrorCode.TRADE_STATUS_INVALID, "当前订单状态不可取消");
        }
        tradeOrderRepository.save(order);
        tradeNotifyHelper.depositCancelled(order, "订单已由当事人取消");
        log.info("cancel by operator ok orderId={} fromStatus={} operatorId={}",
                order.getId(), status, cmd.getOperatorId());
        return Result.success(TradeAssembler.toDTO(order));
    }

    /**
     * 取消所有超时未付定金订单，返回成功取消条数。
     */
    public int cancelExpired() {
        List<TradeOrder> expired = tradeOrderRepository.findExpiredDepositPending(LocalDateTime.now());
        // 无超时订单则返回 0
        if (expired.isEmpty()) {
            return 0;
        }
        int cancelled = 0;
        // 逐笔尝试取消超时待付单
        for (TradeOrder order : expired) {
            // 单笔成功则计数 +1
            if (cancelExpiredOne(order)) {
                cancelled++;
            }
        }
        return cancelled;
    }

    /**
     * 单笔超时释放：先条件取消再解锁，避免覆盖已支付订单。
     */
    private boolean cancelExpiredOne(TradeOrder order) {
        try {
            LocalDateTime cancelledAt = LocalDateTime.now();
            // 仅 DEPOSIT_PENDING 可改；已支付则跳过
            if (!tradeOrderRepository.tryCancelExpiredPending(order.getId(), cancelledAt)) {
                log.info("expire deposit skip orderId={} status already changed", order.getId());
                return false;
            }
            releaseSupply(order);
            tradeNotifyHelper.depositExpired(order);
            return true;
        } catch (Exception ex) {
            log.error("expire deposit fail orderId={} supplyId={}", order.getId(), order.getSupplyId(), ex);
            return false;
        }
    }

    /**
     * 释放供应占用（RESERVING / LOCKED → PUBLISHED）。
     */
    private void releaseSupply(TradeOrder order) {
        // 跨服务：释放供应占用
        Result<Void> unlockRes = supplyFeignClient.unlock(order.getSupplyId(),
                UnlockSupplyCommand.builder().orderId(order.getId()).build());
        // 解锁失败则取消流程中止
        if (unlockRes == null || !ErrorCode.OK.equals(unlockRes.getCode())) {
            throw new BusinessException(unlockRes == null ? ErrorCode.INVALID_UNLOCK : unlockRes.getCode(),
                    unlockRes == null ? "释放供应失败" : unlockRes.getMessage());
        }
    }

    /**
     * 已付定金按原渠道退款，退款失败则中止取消。
     */
    private void refundDeposit(TradeOrder order) {
        BigDecimal amount = order.getDepositAmount().amount();
        // 已付订单必须有原支付渠道才能退款
        if (!StringUtils.hasText(order.getPayChannel())) {
            throw new BusinessException(ErrorCode.PAYMENT_FAILED, "订单缺少支付渠道，无法退款");
        }
        Result<PaymentResultDTO> refundRes = walletFeignClient.refund(WalletRefundCommand.builder()
                .userId(order.getBuyerId())
                .channel(order.getPayChannel())
                .amount(amount)
                .orderId(order.getId())
                .idempotentKey(BIZ_TYPE_REFUND + ":" + order.getId())
                .relatedPaymentId(order.getPaymentId())
                .bizType(BIZ_TYPE_REFUND)
                .remark("取消订单退定金 " + order.getOrderNo())
                .build());
        // 退款失败则不允许取消已付订单
        if (refundRes == null || !ErrorCode.OK.equals(refundRes.getCode())) {
            throw new BusinessException(refundRes == null ? ErrorCode.PAYMENT_FAILED : refundRes.getCode(),
                    refundRes == null ? "定金退款失败" : refundRes.getMessage());
        }
        log.info("refund deposit ok orderId={} channel={} amount={}", order.getId(), order.getPayChannel(), amount);
    }
}
