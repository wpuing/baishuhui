package com.baishuhui.infrastructure.db.mongo.repositories;

import com.baishuhui.common.util.IdUtil;
import com.baishuhui.domain.price.entity.MarketPrice;
import com.baishuhui.domain.price.entity.vo.PriceSnapshot;
import com.baishuhui.domain.price.entity.vo.Unit;
import com.baishuhui.domain.price.repositories.IMarketPriceRepository;
import com.baishuhui.infrastructure.db.mongo.document.PriceHistoryDocument;
import com.baishuhui.infrastructure.cache.RealtimePriceRedisStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 行情仓储：历史写入 Mongo {@code price_history}，实时写入 Redis。
 *
 * @author wei yz
 */
@Slf4j
@Repository
@Profile("!demo & !standalone")
@RequiredArgsConstructor
public class MarketPriceRepositoryImpl implements IMarketPriceRepository {

    private static final int DEFAULT_HISTORY_LIMIT = 50;

    private final MongoTemplate mongoTemplate;
    private final ObjectProvider<RealtimePriceRedisStore> redisStore;
    private final ObjectMapper objectMapper;

    @Value("${bsh.price.realtime-ttl-seconds:30}")
    private long realtimeTtlSeconds;

    /**
     * 按标识查询（与 SKU 相同）。
     */
    @Override
    public Optional<MarketPrice> findById(String id) {
        return findBySku(id);
    }

    /**
     * 追加历史并刷新 Redis 实时价。
     */
    @Override
    public void save(MarketPrice aggregate) {
        if (aggregate == null || !StringUtils.hasText(aggregate.getSku()) || aggregate.getLatest() == null) {
            return;
        }
        PriceSnapshot latest = aggregate.getLatest();
        PriceHistoryDocument doc = new PriceHistoryDocument();
        doc.setId(IdUtil.nextId());
        doc.setSku(aggregate.getSku());
        doc.setPrice(latest.price());
        doc.setUnit(unitCode(aggregate));
        doc.setSnapshotTime(latest.snapshotTime() == null ? LocalDateTime.now() : latest.snapshotTime());
        mongoTemplate.insert(doc);
        putRealtime(doc);
        log.info("price history saved sku={} price={}", doc.getSku(), doc.getPrice());
    }

    /**
     * 按 SKU 删除历史。
     */
    @Override
    public void remove(String id) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        mongoTemplate.remove(Query.query(Criteria.where("sku").is(id.trim())), PriceHistoryDocument.class);
    }

    /**
     * 最新一条历史还原聚合。
     */
    @Override
    public Optional<MarketPrice> findBySku(String sku) {
        if (!StringUtils.hasText(sku)) {
            return Optional.empty();
        }
        Query query = Query.query(Criteria.where("sku").is(sku.trim()))
                .with(Sort.by(Sort.Direction.DESC, "snapshotTime"))
                .limit(1);
        PriceHistoryDocument doc = mongoTemplate.findOne(query, PriceHistoryDocument.class);
        return Optional.ofNullable(toAggregate(doc));
    }

    /**
     * 按 SKU 倒序历史。
     */
    @Override
    public List<PriceSnapshot> listHistory(String sku, int limit) {
        if (!StringUtils.hasText(sku)) {
            return List.of();
        }
        int size = limit <= 0 ? DEFAULT_HISTORY_LIMIT : limit;
        Query query = Query.query(Criteria.where("sku").is(sku.trim()))
                .with(Sort.by(Sort.Direction.DESC, "snapshotTime"))
                .limit(size);
        List<PriceHistoryDocument> rows = mongoTemplate.find(query, PriceHistoryDocument.class);
        List<PriceSnapshot> list = new ArrayList<>(rows.size());
        for (PriceHistoryDocument row : rows) {
            list.add(toSnapshot(row));
        }
        return list;
    }

    private void putRealtime(PriceHistoryDocument doc) {
        RealtimePriceRedisStore store = redisStore.getIfAvailable();
        if (store == null) {
            return;
        }
        Map<String, Object> payload = new LinkedHashMap<>(4);
        payload.put("sku", doc.getSku());
        payload.put("price", doc.getPrice());
        payload.put("unit", doc.getUnit());
        payload.put("ts", System.currentTimeMillis());
        try {
            store.put(doc.getSku(), objectMapper.writeValueAsString(payload),
                    Duration.ofSeconds(Math.max(1L, realtimeTtlSeconds)));
        } catch (JsonProcessingException ex) {
            log.warn("price redis json fail sku={}", doc.getSku(), ex);
        }
    }

    private MarketPrice toAggregate(PriceHistoryDocument doc) {
        if (doc == null || !StringUtils.hasText(doc.getSku())) {
            return null;
        }
        String unitCode = StringUtils.hasText(doc.getUnit()) ? doc.getUnit() : "斤";
        return MarketPrice.create(doc.getSku(), doc.getSku(), new Unit(unitCode, unitCode), doc.getPrice());
    }

    private PriceSnapshot toSnapshot(PriceHistoryDocument doc) {
        String unitCode = StringUtils.hasText(doc.getUnit()) ? doc.getUnit() : "斤";
        return new PriceSnapshot(doc.getPrice(), new Unit(unitCode, unitCode), doc.getSnapshotTime());
    }

    private String unitCode(MarketPrice aggregate) {
        if (aggregate.getUnit() != null && StringUtils.hasText(aggregate.getUnit().code())) {
            return aggregate.getUnit().code();
        }
        if (aggregate.getLatest() != null && aggregate.getLatest().unit() != null) {
            return aggregate.getLatest().unit().code();
        }
        return "斤";
    }
}
