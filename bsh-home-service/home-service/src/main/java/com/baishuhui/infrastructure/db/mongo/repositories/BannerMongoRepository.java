package com.baishuhui.infrastructure.db.mongo.repositories;

import com.baishuhui.infrastructure.db.mongo.document.BannerDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Banner Mongo Spring Data 仓储（仅基础设施使用）。
 *
 * @author wei yz
 */
public interface BannerMongoRepository extends MongoRepository<BannerDocument, String> {

    /**
     * 按位置查询启用中的 Banner（权重降序）。
     *
     * @param position 位置
     * @return 文档列表
     */
    List<BannerDocument> findByEnabledTrueAndPositionOrderByWeightDesc(String position);
}
