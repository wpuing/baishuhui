package com.baishuhui.infrastructure.db.mongo.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MongoDB 行情历史文档。
 *
 * @author wei yz
 */
@Data
@Document(collection = "price_history")
@CompoundIndex(name = "idx_sku_snapshot", def = "{'sku': 1, 'snapshotTime': -1}")
public class PriceHistoryDocument {

    @Id
    private String id;

    private String sku;

    private BigDecimal price;

    private String unit;

    private LocalDateTime snapshotTime;
}
