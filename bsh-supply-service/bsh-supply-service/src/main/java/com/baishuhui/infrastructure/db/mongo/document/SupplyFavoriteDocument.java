package com.baishuhui.infrastructure.db.mongo.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * 供应收藏 Mongo 文档。
 *
 * @author wei yz
 */
@Data
@Document("supply_favorite")
@CompoundIndex(name = "uk_user_supply", def = "{'user_id':1,'supply_id':1}", unique = true)
public class SupplyFavoriteDocument {

    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("supply_id")
    private String supplyId;

    @Field("created_at")
    private LocalDateTime createdAt;
}
