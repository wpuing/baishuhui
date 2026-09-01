package com.baishuhui.infrastructure.db.mongo.repositories;

import com.baishuhui.domain.supply.entity.SupplyFavorite;
import com.baishuhui.domain.supply.repositories.ISupplyFavoriteRepository;
import com.baishuhui.infrastructure.db.mongo.document.SupplyFavoriteDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 供应收藏 Mongo 实现。
 *
 * @author wei yz
 */
@Repository
@Profile("!demo")
@RequiredArgsConstructor
public class SupplyFavoriteRepositoryImpl implements ISupplyFavoriteRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public void save(SupplyFavorite favorite) {
        SupplyFavoriteDocument doc = new SupplyFavoriteDocument();
        doc.setId(favorite.getId());
        doc.setUserId(favorite.getUserId());
        doc.setSupplyId(favorite.getSupplyId());
        doc.setCreatedAt(favorite.getCreatedAt());
        mongoTemplate.save(doc);
    }

    @Override
    public void remove(String userId, String supplyId) {
        Query query = Query.query(Criteria.where("user_id").is(userId).and("supply_id").is(supplyId));
        mongoTemplate.remove(query, SupplyFavoriteDocument.class);
    }

    @Override
    public Optional<SupplyFavorite> find(String userId, String supplyId) {
        Query query = Query.query(Criteria.where("user_id").is(userId).and("supply_id").is(supplyId));
        SupplyFavoriteDocument doc = mongoTemplate.findOne(query, SupplyFavoriteDocument.class);
        return Optional.ofNullable(doc).map(this::toDomain);
    }

    @Override
    public List<SupplyFavorite> listByUser(String userId, int limit) {
        int size = Math.min(Math.max(limit, 1), 200);
        Query query = Query.query(Criteria.where("user_id").is(userId))
                .with(Sort.by(Sort.Direction.DESC, "created_at"))
                .limit(size);
        return mongoTemplate.find(query, SupplyFavoriteDocument.class).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean exists(String userId, String supplyId) {
        Query query = Query.query(Criteria.where("user_id").is(userId).and("supply_id").is(supplyId));
        return mongoTemplate.exists(query, SupplyFavoriteDocument.class);
    }

    private SupplyFavorite toDomain(SupplyFavoriteDocument doc) {
        return new SupplyFavorite(doc.getId(), doc.getUserId(), doc.getSupplyId(), doc.getCreatedAt());
    }
}
