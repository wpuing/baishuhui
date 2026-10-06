package com.baishuhui.application.service.order;

import com.baishuhui.order.vo.TradeNodeDTO;
import com.baishuhui.order.vo.TradeOrderDTO;
import com.baishuhui.supply.vo.SupplyInfoDTO;
import com.baishuhui.domain.order.entity.TradeOrder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 交易订单领域模型与 DTO 组装。
 *
 * @author wei yz
 */
public final class TradeAssembler {
    private static final List<String> STATUS_FLOW = List.of(
            TradeOrder.DEPOSIT_PENDING,
            TradeOrder.PLACED,
            TradeOrder.PENDING_CONFIRM,
            TradeOrder.CONFIRMED,
            TradeOrder.IN_PROGRESS,
            TradeOrder.COMPLETED
    );

    private TradeAssembler() {}

    /**
     * 转换为 DTO。
     */
    public static TradeOrderDTO toDTO(TradeOrder order) {
        return toDTO(order, null);
    }

    /**
     * 转换为 DTO，并附带供应摘要与流转节点。
     */
    public static TradeOrderDTO toDTO(TradeOrder order, SupplyInfoDTO supply) {
        String status = TradeOrder.normalizeStatus(order.getStatus());
        String location = supply == null ? null : supply.getLocation();
        TradeOrderDTO.TradeOrderDTOBuilder builder = TradeOrderDTO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .supplyId(order.getSupplyId())
                .buyerId(order.getBuyerId())
                .sellerId(order.getSellerId())
                .category(order.getCategory())
                .depositAmount(order.getDepositAmount().amount())
                .dealAmount(order.getDealAmount().amount())
                .status(status)
                .statusLabel(TradeOrder.statusLabel(status))
                .createdAt(order.getCreatedAt())
                .pendingConfirmAt(order.getPendingConfirmAt())
                .confirmedAt(order.getConfirmedAt())
                .inProgressAt(order.getInProgressAt())
                .completedAt(order.getCompletedAt())
                .payChannel(order.getPayChannel())
                .paymentId(order.getPaymentId())
                .depositExpireAt(order.getDepositExpireAt())
                .nodes(buildNodes(order, status, location));
        // 空值分支判断
        if (supply != null) {
            builder.supplyTitle(supply.getTitle())
                    .supplyLocation(supply.getLocation());
            // 空值分支判断
            if (supply.getFieldImages() != null && !supply.getFieldImages().isEmpty()) {
                builder.coverImage(supply.getFieldImages().get(0));
            }
        }
        return builder.build();
    }

    /**
     * 组装时间轴；历史单缺中间时间时仍展示已到达，时间标为空，已取消单在末尾追加取消节点。
     */
    private static List<TradeNodeDTO> buildNodes(TradeOrder order, String status, String origin) {
        int cur = currentIndex(order, status);
        boolean skippedPending = order.getPendingConfirmAt() == null && order.getConfirmedAt() != null;
        boolean cancelled = TradeOrder.CANCELLED.equals(status);
        List<TradeNodeDTO> nodes = new ArrayList<>(7);
        nodes.add(node(TradeOrder.DEPOSIT_PENDING, "待付定金", "已锁定预定，等待从账户支付定金",
                order.getCreatedAt(), onlinePlace("线上下定金", origin), cur, skippedPending, cancelled));
        // 未支付定金前不展示拍下时间，避免与下单时间混淆
        boolean paid = cur >= STATUS_FLOW.indexOf(TradeOrder.PLACED) && !TradeOrder.DEPOSIT_PENDING.equals(status);
        nodes.add(node(TradeOrder.PLACED, "已拍下", "买家支付定金，锁定供应",
                paid ? order.getCreatedAt() : null,
                onlinePlace("线上拍下", origin), cur, skippedPending, cancelled));
        nodes.add(node(TradeOrder.PENDING_CONFIRM, "待确认",
                skippedPending ? "未单独提交确认，已直接进入确认" : "等待买卖双方确认成交意向",
                order.getPendingConfirmAt(), onlinePlace("线上待确认", origin), cur, skippedPending, cancelled));
        nodes.add(node(TradeOrder.CONFIRMED, "已确认", "双方确认成交，可安排线下交割",
                order.getConfirmedAt(), onlinePlace("线上确认", origin), cur, skippedPending, cancelled));
        nodes.add(node(TradeOrder.IN_PROGRESS, "线下交易进行中", "约定到产地当面看货、交割",
                order.getInProgressAt(), offlinePlace(origin), cur, skippedPending, cancelled));
        nodes.add(node(TradeOrder.COMPLETED, "已结单", "线下交割完成并结单",
                order.getCompletedAt(), offlinePlace(origin), cur, skippedPending, cancelled));
        // 业务条件分支
        if (cancelled) {
            nodes.add(TradeNodeDTO.builder()
                    .status(TradeOrder.CANCELLED)
                    .label(TradeOrder.statusLabel(TradeOrder.CANCELLED))
                    .detail(order.getPayChannel() == null ? "未付定金已取消预定" : "已取消并退回定金")
                    .occurredAt(order.getCancelledAt())
                    .location(onlinePlace("线上取消", origin))
                    .reached(true)
                    .current(true)
                    .build());
        }
        return nodes;
    }

    /**
     * 已取消单以是否付过定金判断停留阶段，避免时间轴全灰。
     */
    private static int currentIndex(TradeOrder order, String status) {
        // 字段相等性校验
        if (TradeOrder.CANCELLED.equals(status)) {
            return order.getPayChannel() == null
                    ? STATUS_FLOW.indexOf(TradeOrder.DEPOSIT_PENDING)
                    : STATUS_FLOW.indexOf(TradeOrder.PLACED);
        }
        int idx = STATUS_FLOW.indexOf(status);
        return idx < 0 ? 0 : idx;
    }

    private static TradeNodeDTO node(
            String code,
            String label,
            String detail,
            LocalDateTime at,
            String location,
            int currentIndex,
            boolean skippedPending,
            boolean cancelled) {
        int idx = STATUS_FLOW.indexOf(code);
        boolean skipped = TradeOrder.PENDING_CONFIRM.equals(code) && skippedPending;
        boolean reached = currentIndex >= idx || skipped;
        return TradeNodeDTO.builder()
                .status(code)
                .label(label)
                .detail(detail)
                .occurredAt(at)
                .location(location)
                .reached(reached)
                .current(!cancelled && !skipped && idx == currentIndex)
                .skipped(skipped)
                .build();
    }

    private static String onlinePlace(String action, String origin) {
        // 空值分支判断
        if (origin == null || origin.isBlank()) {
            return action;
        }
        return action + " · 产地 " + origin;
    }

    private static String offlinePlace(String origin) {
        // 空值分支判断
        if (origin == null || origin.isBlank()) {
            return "线下产地（待补充）";
        }
        return "线下产地 · " + origin;
    }
}
