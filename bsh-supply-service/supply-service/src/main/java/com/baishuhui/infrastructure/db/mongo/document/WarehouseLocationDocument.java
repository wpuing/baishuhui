package com.baishuhui.infrastructure.db.mongo.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 仓位文档。
 *
 * @author wei yz
 */
@Data
@Document(collection = "warehouse_location")
public class WarehouseLocationDocument {
    @Id
    private String id;
    @Field("merchant_id")
    private String merchantId;
    private String name;
    private String remark;
    private String status;
}
