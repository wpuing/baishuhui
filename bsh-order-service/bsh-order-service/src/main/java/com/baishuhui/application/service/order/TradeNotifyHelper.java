package com.baishuhui.application.service.order;

import com.baishuhui.client.user.feign.INotificationFeignService;
import com.baishuhui.domain.order.entity.TradeOrder;
import com.baishuhui.user.vo.notify.CreateNotificationCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 交易节点站内消息：失败仅记日志，不阻断主流程。
 *
 * @author wei yz
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TradeNotifyHelper {

    private final INotificationFeignService notificationFeignService;

    public void depositPending(TradeOrder order) {
        notifyQuiet(order.getBuyerId(), "DEPOSIT_PENDING", "请尽快支付定金",
                "订单 " + order.getOrderNo() + " 已预定成功，请在超时前支付定金，超时将自动释放供应。",
                order);
        notifyQuiet(order.getSellerId(), "DEPOSIT_PENDING", "有人预定了您的供应",
                "买家已预定订单 " + order.getOrderNo() + "，等待对方支付定金。",
                order);
    }

    public void depositPaid(TradeOrder order) {
        notifyQuiet(order.getBuyerId(), "DEPOSIT_PAID", "定金支付成功",
                "订单 " + order.getOrderNo() + " 定金已支付，供应已锁定，请继续确认采购。",
                order);
        notifyQuiet(order.getSellerId(), "DEPOSIT_PAID", "买家已付定金",
                "订单 " + order.getOrderNo() + " 定金已到账，供应已锁定。",
                order);
    }

    public void depositCancelled(TradeOrder order, String reason) {
        String tip = reason == null ? "订单已取消" : reason;
        notifyQuiet(order.getBuyerId(), "DEPOSIT_CANCELLED", "订单已取消",
                "订单 " + order.getOrderNo() + "：" + tip,
                order);
        notifyQuiet(order.getSellerId(), "DEPOSIT_CANCELLED", "订单已取消",
                "订单 " + order.getOrderNo() + "：" + tip,
                order);
    }

    public void depositExpired(TradeOrder order) {
        notifyQuiet(order.getBuyerId(), "DEPOSIT_EXPIRED", "定金支付超时",
                "订单 " + order.getOrderNo() + " 超时未付定金，供应已释放，可重新预定。",
                order);
        notifyQuiet(order.getSellerId(), "DEPOSIT_EXPIRED", "预定已超时释放",
                "订单 " + order.getOrderNo() + " 买家超时未付定金，供应已恢复可采购。",
                order);
    }

    public void tradeConfirmed(TradeOrder order) {
        notifyQuiet(order.getBuyerId(), "TRADE_CONFIRMED", "采购已确认",
                "订单 " + order.getOrderNo() + " 已确认，可开始线下交割。",
                order);
        notifyQuiet(order.getSellerId(), "TRADE_CONFIRMED", "采购已确认",
                "订单 " + order.getOrderNo() + " 已确认，可开始线下交割。",
                order);
    }

    public void tradeCompleted(TradeOrder order) {
        notifyQuiet(order.getBuyerId(), "TRADE_COMPLETED", "交易已结单",
                "订单 " + order.getOrderNo() + " 已结单，钱包已按规则结算。",
                order);
        notifyQuiet(order.getSellerId(), "TRADE_COMPLETED", "交易已结单",
                "订单 " + order.getOrderNo() + " 已结单，货款已入账。",
                order);
    }

    private void notifyQuiet(String userId, String type, String title, String content, TradeOrder order) {
        if (userId == null || userId.isBlank()) {
            return;
        }
        try {
            notificationFeignService.create(CreateNotificationCommand.builder()
                    .userId(userId)
                    .msgType(type)
                    .title(title)
                    .content(content)
                    .bizType("ORDER")
                    .bizId(order.getId())
                    .build());
        } catch (Exception ex) {
            log.warn("trade notify fail userId={} type={} orderId={}", userId, type, order.getId(), ex);
        }
    }
}
