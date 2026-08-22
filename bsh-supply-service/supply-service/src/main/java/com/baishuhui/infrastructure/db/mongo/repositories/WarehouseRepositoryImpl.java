package com.baishuhui.infrastructure.db.mongo.repositories;

import com.baishuhui.domain.supply.entity.StockMove;
import com.baishuhui.domain.supply.entity.WarehouseLocation;
import com.baishuhui.domain.supply.entity.WarehouseStock;
import com.baishuhui.domain.supply.repositories.IWarehouseRepository;
import com.baishuhui.infrastructure.db.mongo.document.WarehouseLocationDocument;
import com.baishuhui.infrastructure.db.mongo.document.WarehouseMoveDocument;
import com.baishuhui.infrastructure.db.mongo.document.WarehouseStockDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 仓库 Mongo 仓储。
 *
 * @author wei yz
 */
@Repository
@Profile("!demo")
@RequiredArgsConstructor
public class WarehouseRepositoryImpl implements IWarehouseRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<WarehouseLocation> findLocation(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, WarehouseLocationDocument.class)).map(this::toLocation);
    }

    @Override
    public List<WarehouseLocation> listLocations(String merchantId) {
        Query query = Query.query(Criteria.where("merchant_id").is(merchantId));
        return mongoTemplate.find(query, WarehouseLocationDocument.class).stream()
                .map(this::toLocation)
                .collect(Collectors.toList());
    }

    @Override
    public void saveLocation(WarehouseLocation location) {
        WarehouseLocationDocument doc = new WarehouseLocationDocument();
        doc.setId(location.getId());
        doc.setMerchantId(location.getMerchantId());
        doc.setName(location.getName());
        doc.setRemark(location.getRemark());
        doc.setStatus(location.getStatus());
        mongoTemplate.save(doc);
    }

    @Override
    public void removeLocation(String id) {
        WarehouseLocationDocument doc = mongoTemplate.findById(id, WarehouseLocationDocument.class);
        if (doc != null) {
            mongoTemplate.remove(doc);
        }
    }

    @Override
    public Optional<WarehouseStock> findStock(String merchantId, String locationId, String category, String unit) {
        Query query = Query.query(Criteria.where("merchant_id").is(merchantId)
                .and("location_id").is(locationId)
                .and("category").is(category)
                .and("unit").is(unit));
        return Optional.ofNullable(mongoTemplate.findOne(query, WarehouseStockDocument.class)).map(this::toStock);
    }

    @Override
    public List<WarehouseStock> listStocks(String merchantId, String locationId) {
        Criteria criteria = Criteria.where("merchant_id").is(merchantId);
        if (StringUtils.hasText(locationId)) {
            criteria = criteria.and("location_id").is(locationId);
        }
        return mongoTemplate.find(Query.query(criteria), WarehouseStockDocument.class).stream()
                .map(this::toStock)
                .collect(Collectors.toList());
    }

    @Override
    public void saveStock(WarehouseStock stock) {
        WarehouseStockDocument doc = new WarehouseStockDocument();
        doc.setId(stock.getId());
        doc.setMerchantId(stock.getMerchantId());
        doc.setLocationId(stock.getLocationId());
        doc.setCategory(stock.getCategory());
        doc.setUnit(stock.getUnit());
        doc.setQuantity(stock.getQuantity());
        mongoTemplate.save(doc);
    }

    @Override
    public void saveMove(StockMove move) {
        WarehouseMoveDocument doc = new WarehouseMoveDocument();
        doc.setId(move.getId());
        doc.setMerchantId(move.getMerchantId());
        doc.setLocationId(move.getLocationId());
        doc.setCategory(move.getCategory());
        doc.setUnit(move.getUnit());
        doc.setQuantity(move.getQuantity());
        doc.setDirection(move.getDirection());
        doc.setRemark(move.getRemark());
        doc.setCreatedAt(move.getCreatedAt());
        mongoTemplate.save(doc);
    }

    @Override
    public List<StockMove> listMoves(String merchantId, int limit) {
        Query query = Query.query(Criteria.where("merchant_id").is(merchantId))
                .with(Sort.by(Sort.Direction.DESC, "created_at"))
                .limit(Math.max(1, limit));
        return mongoTemplate.find(query, WarehouseMoveDocument.class).stream()
                .map(this::toMove)
                .collect(Collectors.toList());
    }

    private WarehouseLocation toLocation(WarehouseLocationDocument doc) {
        return WarehouseLocation.restore(doc.getId(), doc.getMerchantId(), doc.getName(), doc.getRemark(), doc.getStatus());
    }

    private WarehouseStock toStock(WarehouseStockDocument doc) {
        return WarehouseStock.restore(
                doc.getId(), doc.getMerchantId(), doc.getLocationId(),
                doc.getCategory(), doc.getUnit(), doc.getQuantity());
    }

    private StockMove toMove(WarehouseMoveDocument doc) {
        return new StockMove(
                doc.getId(), doc.getMerchantId(), doc.getLocationId(),
                doc.getCategory(), doc.getUnit(), doc.getQuantity(),
                doc.getDirection(), doc.getRemark(), doc.getCreatedAt());
    }
}
