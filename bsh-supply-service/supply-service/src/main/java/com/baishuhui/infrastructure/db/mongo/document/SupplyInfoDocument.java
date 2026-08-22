package com.baishuhui.infrastructure.db.mongo.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 供应信息 Mongo 文档。
 *
 * @author wei yz
 */
@Data
@Document(collection = "supply_info")
public class SupplyInfoDocument {

    @Id
    private String id;

    @Field("merchant_id")
    private String merchantId;

    private String title;
    private String description;

    @Field("contact_phone")
    private String contactPhone;

    private String location;
    private String category;
    private String unit;
    private BigDecimal quantity;
    private BigDecimal price;

    @Field("deposit_amount")
    private BigDecimal depositAmount;

    private String status;

    @Field("publish_time")
    private LocalDateTime publishTime;

    @Field("field_images")
    private List<String> fieldImages;

    @Field("completion_images")
    private List<String> completionImages;

    @Field("locked_by_buyer_id")
    private String lockedByBuyerId;

    @Field("lock_order_id")
    private String lockOrderId;

    /** 兼容旧字段 */
    private List<String> images;
}
