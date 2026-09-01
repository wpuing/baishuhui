package com.baishuhui.order.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 交易订单传输对象。
 *
 * @author wei yz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeOrderDTO {
    private String id;
    private String orderNo;
    private String supplyId;
    private String buyerId;
    private String sellerId;
    private String category;
    private BigDecimal depositAmount;
    private BigDecimal dealAmount;
    private String status;
    /** 中文状态展示 */
    private String statusLabel;
    /** 供应标题（列表 enrichment） */
    private String supplyTitle;
    /** 供应产地 */
    private String supplyLocation;
    /** 封面图 */
    private String coverImage;
    private LocalDateTime createdAt;
    private LocalDateTime pendingConfirmAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime inProgressAt;
    private LocalDateTime completedAt;
    /** 定金支付渠道 */
    private String payChannel;
    /** 定金支付单号 */
    private String paymentId;
    /** 待付定金超时时间 */
    private LocalDateTime depositExpireAt;
    /** 流转节点（含时间、地点） */
    @Builder.Default
    private List<TradeNodeDTO> nodes = new ArrayList<>();
}
