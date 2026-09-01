package com.baishuhui.infrastructure.db.mongo.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

/**
 * 库存文档。
 *
 * @author wei yz
 */
@Data
@Document(collection = "warehouse_stock")
public class WarehouseStockDocument {
    @Id
    private String id;
    @Field("merchant_id")
    private String merchantId;
    @Field("location_id")
    private String locationId;
    private String category;
    private String unit;
    private BigDecimal quantity;
}
