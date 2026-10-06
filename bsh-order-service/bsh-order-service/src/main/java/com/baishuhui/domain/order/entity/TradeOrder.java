package com.baishuhui.domain.order.entity;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.ddd.AggregateRoot;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.domain.order.event.TradeOrderCompletedEvent;
import com.baishuhui.domain.order.event.TradeOrderCreatedEvent;
import com.baishuhui.domain.order.entity.vo.Money;
import com.baishuhui.order.constant.TradeOrderStatusConstants;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 农产品交易订单：待付定金 → 已拍下 → 待确认 → 已确认 → 线下进行中 → 已结单，支持取消。
 *
 * @author wei yz
 */
@Getter
public class TradeOrder extends AggregateRoot<String> {

    /** @see TradeOrderStatusConstants#DEPOSIT_PENDING */
    public static final String DEPOSIT_PENDING = TradeOrderStatusConstants.DEPOSIT_PENDING;

    /** @see TradeOrderStatusConstants#PLACED */
    public static final String PLACED = TradeOrderStatusConstants.PLACED;

    /** @see TradeOrderStatusConstants#PENDING_CONFIRM */
    public static final String PENDING_CONFIRM = TradeOrderStatusConstants.PENDING_CONFIRM;

    /** @see TradeOrderStatusConstants#CONFIRMED */
    public static final String CONFIRMED = TradeOrderStatusConstants.CONFIRMED;

    /** @see TradeOrderStatusConstants#IN_PROGRESS */
    public static final String IN_PROGRESS = TradeOrderStatusConstants.IN_PROGRESS;

    /** @see TradeOrderStatusConstants#COMPLETED */
    public static final String COMPLETED = TradeOrderStatusConstants.COMPLETED;

    /** @see TradeOrderStatusConstants#CANCELLED */
    public static final String CANCELLED = TradeOrderStatusConstants.CANCELLED;

    /** 待付定金默认超时时长（分钟） */
    private static final int DEPOSIT_EXPIRE_MINUTES = 30;

    /**
     * 旧状态兼容：历史库值 DEPOSIT_PAID 等同 PLACED。
     *
     * @deprecated 仅兼容读库，新写入请用 {@link #PLACED}
     * @see TradeOrderStatusConstants#DEPOSIT_PAID
     */
    @Deprecated
    public static final String DEPOSIT_PAID = TradeOrderStatusConstants.DEPOSIT_PAID;

    private String orderNo;
    private String supplyId;
    private String buyerId;
    private String sellerId;
    private String category;
    private Money depositAmount;
    private Money dealAmount;
    private String status;
    private LocalDateTime createdAt;
    /** 提交确认真发生时间；直接确认时可为空 */
    private LocalDateTime pendingConfirmAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime inProgressAt;
    private LocalDateTime completedAt;
    /** 定金支付渠道（支付成功后写入） */
    private String payChannel;
    /** 定金支付单号（支付成功后写入） */
    private String paymentId;
    /** 待付定金超时时间 */
    private LocalDateTime depositExpireAt;
    private LocalDateTime cancelledAt;

    protected TradeOrder() {
    }

    private TradeOrder(
            String id,
            String orderNo,
            String supplyId,
            String buyerId,
            String sellerId,
            String category,
            Money depositAmount,
            Money dealAmount
    ) {
        super(id);
        this.orderNo = orderNo;
        this.supplyId = supplyId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.category = category;
        this.depositAmount = depositAmount;
        this.dealAmount = dealAmount;
        this.status = DEPOSIT_PENDING;
        this.createdAt = LocalDateTime.now();
        this.depositExpireAt = this.createdAt.plusMinutes(DEPOSIT_EXPIRE_MINUTES);
    }

    /**
     * 下定金：生成待付定金订单，校验买卖双方并注册创建事件。
     */
    public static TradeOrder placeDeposit(
            String id,
            String orderNo,
            String supplyId,
            String buyerId,
            String sellerId,
            String category,
            BigDecimal deposit,
            BigDecimal dealAmount
    ) {
        // 空值分支判断
        if (buyerId == null || buyerId.isBlank() || sellerId == null || sellerId.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_TRADE, "买卖双方不能为空");
        }
        // 禁止自买自卖，避免刷锁供应与虚假成交
        if (buyerId.equals(sellerId)) {
            throw new BusinessException(ErrorCode.INVALID_TRADE, "不能拍下自己发布的供应");
        }
        TradeOrder order = new TradeOrder(
                id,
                orderNo,
                supplyId,
                buyerId,
                sellerId,
                category,
                Money.ofCny(deposit),
                Money.ofCny(dealAmount)
        );
        order.registerEvent(new TradeOrderCreatedEvent(id, orderNo, supplyId, buyerId, sellerId));
        return order;
    }

    /**
     * 覆盖待付定金超时（分钟），供配置注入；非法值回退默认 30。
     *
     * @param minutes 超时分钟数，建议 5～1440
     */
    public void applyDepositExpireMinutes(int minutes) {
        int safe = minutes < 1 ? DEPOSIT_EXPIRE_MINUTES : Math.min(minutes, 24 * 60);
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.depositExpireAt = this.createdAt.plusMinutes(safe);
    }

    /**
     * 从持久化状态重建交易订单；旧值 DEPOSIT_PAID 映射为 PLACED。
     */
    public static TradeOrder reconstitute(
            String id,
            String orderNo,
            String supplyId,
            String buyerId,
            String sellerId,
            String category,
            Money depositAmount,
            Money dealAmount,
            String status,
            LocalDateTime createdAt,
            LocalDateTime completedAt,
            LocalDateTime pendingConfirmAt,
            LocalDateTime confirmedAt,
            LocalDateTime inProgressAt,
            String payChannel,
            String paymentId,
            LocalDateTime depositExpireAt,
            LocalDateTime cancelledAt
    ) {
        TradeOrder order = new TradeOrder(id, orderNo, supplyId, buyerId, sellerId, category, depositAmount, dealAmount);
        order.status = normalizeStatus(status);
        order.createdAt = createdAt;
        order.completedAt = completedAt;
        order.pendingConfirmAt = pendingConfirmAt;
        order.confirmedAt = confirmedAt;
        order.inProgressAt = inProgressAt;
        order.payChannel = payChannel;
        order.paymentId = paymentId;
        order.depositExpireAt = depositExpireAt;
        order.cancelledAt = cancelledAt;
        return order;
    }

    /**
     * 待付定金 → 已拍下（账户扣款成功）。
     */
    public void markPaid(String channel, String paymentId) {
        // 字段相等性校验
        if (!DEPOSIT_PENDING.equals(status)) {
            throw new BusinessException(ErrorCode.TRADE_STATUS_INVALID, "仅待付定金订单可标记已支付");
        }
        // 空值分支判断
        if (channel == null || channel.isBlank()) {
            throw new BusinessException(ErrorCode.PAYMENT_FAILED, "支付渠道不能为空");
        }
        this.status = PLACED;
        this.payChannel = channel;
        this.paymentId = paymentId;
    }

    /**
     * 待付定金 → 已取消（超时未付 / 买家取消预定，无需退款）。
     */
    public void cancelPending() {
        // 字段相等性校验
        if (!DEPOSIT_PENDING.equals(status)) {
            throw new BusinessException(ErrorCode.TRADE_STATUS_INVALID, "仅待付定金订单可取消预定");
        }
        this.status = CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    /**
     * 已拍下 / 待确认 → 已取消（定金已支付，需先退款）。
     */
    public void cancelPaid() {
        // 字段相等性校验
        if (!PLACED.equals(status) && !PENDING_CONFIRM.equals(status)) {
            throw new BusinessException(ErrorCode.TRADE_STATUS_INVALID, "仅已拍下/待确认订单可取消");
        }
        this.status = CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    /**
     * 待付定金是否已超时。
     */
    public boolean depositExpired(LocalDateTime now) {
        return depositExpireAt != null && now != null && now.isAfter(depositExpireAt);
    }

    /**
     * 已拍下 → 待确认。
     */
    public void submitConfirm() {
        // 字段相等性校验
        if (!PLACED.equals(status)) {
            throw new BusinessException(ErrorCode.TRADE_STATUS_INVALID, "仅已拍下订单可提交确认");
        }
        this.status = PENDING_CONFIRM;
        this.pendingConfirmAt = LocalDateTime.now();
    }

    /**
     * 已拍下或待确认 → 已确认。
     */
    public void confirm() {
        // 字段相等性校验
        if (!PLACED.equals(status) && !PENDING_CONFIRM.equals(status)) {
            throw new BusinessException(ErrorCode.TRADE_STATUS_INVALID, "仅已拍下/待确认订单可确认");
        }
        this.status = CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }

    /**
     * 已确认 → 线下交易进行中。
     */
    public void startTrade() {
        // 字段相等性校验
        if (!CONFIRMED.equals(status)) {
            throw new BusinessException(ErrorCode.TRADE_STATUS_INVALID, "仅已确认订单可开始线下交易");
        }
        this.status = IN_PROGRESS;
        this.inProgressAt = LocalDateTime.now();
    }

    /**
     * 结单前修订成交额：仅已确认 / 进行中，且不得低于已付定金。
     *
     * @param amount 新成交额
     */
    public void reviseDealAmount(BigDecimal amount) {
        // 字段相等性校验
        if (!CONFIRMED.equals(status) && !IN_PROGRESS.equals(status)) {
            throw new BusinessException(ErrorCode.TRADE_STATUS_INVALID, "仅已确认/进行中订单可改成交额");
        }
        Money next = Money.ofCny(amount);
        // 空值分支判断
        if (depositAmount != null && next.amount().compareTo(depositAmount.amount()) < 0) {
            throw new BusinessException(ErrorCode.INVALID_MONEY, "成交额不能低于已付定金");
        }
        this.dealAmount = next;
    }

    /**
     * 已确认或进行中 → 交易完成。
     */
    public void complete() {
        // 字段相等性校验
        if (!CONFIRMED.equals(status) && !IN_PROGRESS.equals(status)) {
            throw new BusinessException(ErrorCode.TRADE_STATUS_INVALID, "仅已确认/进行中订单可结单");
        }
        this.status = COMPLETED;
        this.completedAt = LocalDateTime.now();
        registerEvent(new TradeOrderCompletedEvent(getId(), orderNo, supplyId, category, dealAmount.amount()));
    }

    /**
     * 归一化历史状态。
     */
    public static String normalizeStatus(String raw) {
        // 空值分支判断
        if (raw == null || raw.isBlank()) {
            return PLACED;
        }
        // 字段相等性校验
        if (DEPOSIT_PAID.equals(raw)) {
            return PLACED;
        }
        return raw;
    }

    /**
     * 中文状态文案。
     */
    public static String statusLabel(String status) {
        String s = normalizeStatus(status);
        return switch (s) {
            case DEPOSIT_PENDING -> "待付定金";
            case PLACED -> "已拍下";
            case PENDING_CONFIRM -> "待确认";
            case CONFIRMED -> "已确认";
            case IN_PROGRESS -> "线下交易进行中";
            case COMPLETED -> "已结单";
            case CANCELLED -> "已取消";
            default -> s;
        };
    }
}
