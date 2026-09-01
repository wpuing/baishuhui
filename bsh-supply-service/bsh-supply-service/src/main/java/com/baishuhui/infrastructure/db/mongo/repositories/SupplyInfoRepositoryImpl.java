package com.baishuhui.infrastructure.db.mongo.repositories;

import com.baishuhui.domain.supply.entity.SupplyInfo;
import com.baishuhui.domain.supply.entity.vo.ImageList;
import com.baishuhui.domain.supply.entity.vo.ProductSpec;
import com.baishuhui.domain.supply.repositories.ISupplyInfoRepository;
import com.baishuhui.infrastructure.db.mongo.document.SupplyInfoDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 供应信息 Mongo 仓储实现。
 *
 * @author wei yz
 */
@Repository
@Profile("!demo")
@RequiredArgsConstructor
public class SupplyInfoRepositoryImpl implements ISupplyInfoRepository {

    private final MongoTemplate mongoTemplate;

    /**
     * 按标识查询。
     */
    @Override

    public Optional<SupplyInfo> findById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, SupplyInfoDocument.class)).map(this::toDomain);
    }

    /**
     * 保存。
     */
    @Override

    public void save(SupplyInfo aggregate) {
        mongoTemplate.save(toDocument(aggregate));
    }

    /**
     * 删除。
     */
    @Override

    public void remove(String id) {
        SupplyInfoDocument doc = mongoTemplate.findById(id, SupplyInfoDocument.class);
        // 空值分支判断
        if (doc != null) {
            mongoTemplate.remove(doc);
        }
    }

    /**
     * findByMerchantId。
     */
    @Override

    public List<SupplyInfo> findByMerchantId(String merchantId) {
        Criteria criteria = Criteria.where("merchant_id").is(merchantId);
        try {
            long numericId = Long.parseLong(merchantId);
            criteria = new Criteria().orOperator(
                    Criteria.where("merchant_id").is(merchantId),
                    Criteria.where("merchant_id").is(numericId));
        } catch (NumberFormatException ignored) {
            // 非数字 id 仅按字符串匹配
        }
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, SupplyInfoDocument.class).stream().map(this::toDomain).collect(Collectors.toList());
    }

    /**
     * findPublished。
     */
    @Override
    public List<SupplyInfo> findPublished() {
        Query query = Query.query(Criteria.where("status").is(SupplyInfo.PUBLISHED));
        return mongoTemplate.find(query, SupplyInfoDocument.class).stream().map(this::toDomain).collect(Collectors.toList());
    }

    /**
     * 公开列表：排除草稿与已下架，限制条数防 OOM。
     */
    @Override
    public List<SupplyInfo> findBrowsable(int limit) {
        int size = Math.min(Math.max(limit, 1), 500);
        Query query = Query.query(Criteria.where("status").nin(SupplyInfo.DRAFT, SupplyInfo.CANCELLED))
                .with(org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Direction.DESC, "publish_time"))
                .limit(size);
        return mongoTemplate.find(query, SupplyInfoDocument.class).stream().map(this::toDomain).collect(Collectors.toList());
    }

    /**
     * 品类 / 产地关键字 / 状态筛选。
     */
    @Override
    public List<SupplyInfo> findBrowsableFiltered(String category, String locationKeyword, String status, int limit) {
        int size = Math.min(Math.max(limit, 1), 500);
        Criteria criteria = Criteria.where("status").nin(SupplyInfo.DRAFT, SupplyInfo.CANCELLED);
        // 指定状态则覆盖默认排除草稿/下架规则中的状态过滤
        if (status != null && !status.isBlank()) {
            criteria = Criteria.where("status").is(status.trim());
        }
        if (category != null && !category.isBlank()) {
            criteria = criteria.and("category").is(category.trim());
        }
        if (locationKeyword != null && !locationKeyword.isBlank()) {
            criteria = criteria.and("location").regex(locationKeyword.trim(), "i");
        }
        Query query = Query.query(criteria)
                .with(org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Direction.DESC, "publish_time"))
                .limit(size);
        return mongoTemplate.find(query, SupplyInfoDocument.class).stream().map(this::toDomain).collect(Collectors.toList());
    }

    /**
     * findAll。
     */
    @Override
    public List<SupplyInfo> findAll() {
        return mongoTemplate.findAll(SupplyInfoDocument.class).stream().map(this::toDomain).collect(Collectors.toList());
    }

    private SupplyInfoDocument toDocument(SupplyInfo aggregate) {
        SupplyInfoDocument doc = new SupplyInfoDocument();
        doc.setId(aggregate.getId());
        doc.setMerchantId(aggregate.getMerchantId());
        doc.setTitle(aggregate.getTitle());
        doc.setDescription(aggregate.getDescription());
        doc.setContactPhone(aggregate.getContactPhone());
        doc.setLocation(aggregate.getLocation());
        doc.setCategory(aggregate.getSpec().category());
        doc.setUnit(aggregate.getSpec().unit());
        doc.setQuantity(aggregate.getSpec().quantity());
        doc.setPrice(aggregate.getPrice());
        doc.setDepositAmount(aggregate.getDepositAmount());
        doc.setStatus(aggregate.getStatus());
        doc.setPublishTime(aggregate.getPublishTime());
        doc.setFieldImages(aggregate.getFieldImages().urls());
        doc.setCompletionImages(aggregate.getCompletionImages().urls());
        doc.setImages(aggregate.getFieldImages().urls());
        doc.setLockedByBuyerId(aggregate.getLockedByBuyerId());
        doc.setLockOrderId(aggregate.getLockOrderId());
        return doc;
    }

    private SupplyInfo toDomain(SupplyInfoDocument doc) {
        List<String> field = doc.getFieldImages() != null ? doc.getFieldImages() : doc.getImages();
        ProductSpec spec = new ProductSpec(doc.getCategory(), doc.getUnit(), doc.getQuantity());
        return SupplyInfo.reconstitute(
                doc.getId(),
                doc.getMerchantId(),
                doc.getTitle(),
                doc.getDescription(),
                doc.getContactPhone(),
                doc.getLocation(),
                spec,
                new ImageList(field),
                new ImageList(doc.getCompletionImages()),
                doc.getPrice(),
                doc.getDepositAmount(),
                doc.getStatus(),
                doc.getPublishTime(),
                doc.getLockedByBuyerId(),
                doc.getLockOrderId()
        );
    }
}
