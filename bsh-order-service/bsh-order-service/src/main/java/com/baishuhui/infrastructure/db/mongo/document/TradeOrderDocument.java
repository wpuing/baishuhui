package com.baishuhui.infrastructure.db.mongo.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易订单 Mongo 文档。
 *
 * @author wei yz
 */
@Data
@Document(collection = "trade_order")
public class TradeOrderDocument {

    @Id
    private String id;

    @Field("order_no")
    private String orderNo;

    @Field("supply_id")
    private String supplyId;

    @Field("buyer_id")
    private String buyerId;

    @Field("seller_id")
    private String sellerId;

    private String category;

    @Field("deposit_amount")
    private BigDecimal depositAmount;

    @Field("deal_amount")
    private BigDecimal dealAmount;

    private String status;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("pending_confirm_at")
    private LocalDateTime pendingConfirmAt;

    @Field("confirmed_at")
    private LocalDateTime confirmedAt;

    @Field("in_progress_at")
    private LocalDateTime inProgressAt;

    @Field("completed_at")
    private LocalDateTime completedAt;

    @Field("pay_channel")
    private String payChannel;

    @Field("payment_id")
    private String paymentId;

    @Field("deposit_expire_at")
    private LocalDateTime depositExpireAt;

    @Field("cancelled_at")
    private LocalDateTime cancelledAt;
}
