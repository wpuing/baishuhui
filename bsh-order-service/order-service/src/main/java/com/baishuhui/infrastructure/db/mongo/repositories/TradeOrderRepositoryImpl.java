package com.baishuhui.infrastructure.db.mongo.repositories;

import com.baishuhui.domain.order.entity.TradeOrder;
import com.baishuhui.domain.order.entity.vo.Money;
import com.baishuhui.domain.order.repositories.ITradeOrderRepository;
import com.baishuhui.infrastructure.db.mongo.document.TradeOrderDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 交易订单 Mongo 仓储实现。
 *
 * @author wei yz
 */
@Repository
@Profile("!demo")
@RequiredArgsConstructor
public class TradeOrderRepositoryImpl implements ITradeOrderRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<TradeOrder> findById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, TradeOrderDocument.class)).map(this::toDomain);
    }

    @Override
    public void save(TradeOrder aggregate) {
        mongoTemplate.save(toDocument(aggregate));
    }

    @Override
    public void remove(String id) {
        TradeOrderDocument doc = mongoTemplate.findById(id, TradeOrderDocument.class);
        if (doc != null) {
            mongoTemplate.remove(doc);
        }
    }

    @Override
    public List<TradeOrder> findCompleted() {
        Query query = Query.query(Criteria.where("status").is(TradeOrder.COMPLETED));
        return mongoTemplate.find(query, TradeOrderDocument.class).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<TradeOrder> findBySupplyId(String supplyId) {
        Query query = Query.query(Criteria.where("supply_id").is(supplyId));
        return mongoTemplate.find(query, TradeOrderDocument.class).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<TradeOrder> findExpiredDepositPending(LocalDateTime now) {
        Query query = Query.query(new Criteria().andOperator(
                Criteria.where("status").is(TradeOrder.DEPOSIT_PENDING),
                Criteria.where("deposit_expire_at").lt(now)));
        // 单批上限，避免积压时一次拉爆内存
        query.limit(200);
        return mongoTemplate.find(query, TradeOrderDocument.class).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean tryCancelExpiredPending(String orderId, LocalDateTime cancelledAt) {
        Query query = Query.query(new Criteria().andOperator(
                Criteria.where("_id").is(orderId),
                Criteria.where("status").is(TradeOrder.DEPOSIT_PENDING)));
        Update update = new Update()
                .set("status", TradeOrder.CANCELLED)
                .set("cancelled_at", cancelledAt);
        var result = mongoTemplate.updateFirst(query, update, TradeOrderDocument.class);
        return result.getModifiedCount() > 0;
    }

    @Override
    public List<TradeOrder> pageByBuyer(String buyerId, String status, int pageNum, int pageSize) {
        Query query = buildPartyQuery("buyer_id", buyerId, status, null, null, null, null);
        applyPage(query, pageNum, pageSize);
        return mongoTemplate.find(query, TradeOrderDocument.class).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countByBuyer(String buyerId, String status) {
        return mongoTemplate.count(buildPartyQuery("buyer_id", buyerId, status, null, null, null, null), TradeOrderDocument.class);
    }

    @Override
    public List<TradeOrder> pageBySeller(
            String sellerId,
            String status,
            String category,
            String keyword,
            LocalDateTime fromInclusive,
            LocalDateTime toExclusive,
            int pageNum,
            int pageSize) {
        Query query = buildPartyQuery("seller_id", sellerId, status, category, keyword, fromInclusive, toExclusive);
        applyPage(query, pageNum, pageSize);
        return mongoTemplate.find(query, TradeOrderDocument.class).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countBySeller(
            String sellerId,
            String status,
            String category,
            String keyword,
            LocalDateTime fromInclusive,
            LocalDateTime toExclusive) {
        return mongoTemplate.count(
                buildPartyQuery("seller_id", sellerId, status, category, keyword, fromInclusive, toExclusive),
                TradeOrderDocument.class);
    }

    @Override
    public List<TradeOrder> listBySeller(
            String sellerId,
            String status,
            String category,
            String keyword,
            LocalDateTime fromInclusive,
            LocalDateTime toExclusive,
            int limit) {
        Query query = buildPartyQuery("seller_id", sellerId, status, category, keyword, fromInclusive, toExclusive);
        query.limit(Math.max(limit, 1));
        return mongoTemplate.find(query, TradeOrderDocument.class).stream().map(this::toDomain).collect(Collectors.toList());
    }

    private Query buildPartyQuery(
            String partyField,
            String partyId,
            String status,
            String category,
            String keyword,
            LocalDateTime fromInclusive,
            LocalDateTime toExclusive) {
        List<Criteria> and = new ArrayList<>();
        and.add(Criteria.where(partyField).is(partyId));
        if (StringUtils.hasText(status)) {
            String normalized = TradeOrder.normalizeStatus(status);
            if (TradeOrder.PLACED.equals(normalized)) {
                and.add(new Criteria().orOperator(
                        Criteria.where("status").is(TradeOrder.PLACED),
                        Criteria.where("status").is(TradeOrder.DEPOSIT_PAID)));
            } else {
                and.add(Criteria.where("status").is(normalized));
            }
        }
        if (StringUtils.hasText(category)) {
            and.add(Criteria.where("category").is(category.trim()));
        }
        if (StringUtils.hasText(keyword)) {
            String kw = Pattern.quote(keyword.trim());
            and.add(new Criteria().orOperator(
                    Criteria.where("order_no").regex(kw, "i"),
                    Criteria.where("category").regex(kw, "i"),
                    Criteria.where("supply_id").regex(kw, "i")));
        }
        if (fromInclusive != null || toExclusive != null) {
            Criteria time = Criteria.where("created_at");
            if (fromInclusive != null) {
                time = time.gte(fromInclusive);
            }
            if (toExclusive != null) {
                time = time.lt(toExclusive);
            }
            and.add(time);
        }
        return Query.query(new Criteria().andOperator(and.toArray(new Criteria[0])))
                .with(Sort.by(Sort.Direction.DESC, "created_at"));
    }

    private void applyPage(Query query, int pageNum, int pageSize) {
        int num = Math.max(pageNum, 1);
        int size = Math.max(pageSize, 1);
        query.skip((long) (num - 1) * size).limit(size);
    }

    private TradeOrderDocument toDocument(TradeOrder order) {
        TradeOrderDocument doc = new TradeOrderDocument();
        doc.setId(order.getId());
        doc.setOrderNo(order.getOrderNo());
        doc.setSupplyId(order.getSupplyId());
        doc.setBuyerId(order.getBuyerId());
        doc.setSellerId(order.getSellerId());
        doc.setCategory(order.getCategory());
        doc.setDepositAmount(order.getDepositAmount().amount());
        doc.setDealAmount(order.getDealAmount().amount());
        doc.setStatus(order.getStatus());
        doc.setCreatedAt(order.getCreatedAt());
        doc.setPendingConfirmAt(order.getPendingConfirmAt());
        doc.setConfirmedAt(order.getConfirmedAt());
        doc.setInProgressAt(order.getInProgressAt());
        doc.setCompletedAt(order.getCompletedAt());
        doc.setPayChannel(order.getPayChannel());
        doc.setPaymentId(order.getPaymentId());
        doc.setDepositExpireAt(order.getDepositExpireAt());
        doc.setCancelledAt(order.getCancelledAt());
        return doc;
    }

    private TradeOrder toDomain(TradeOrderDocument doc) {
        return TradeOrder.reconstitute(
                doc.getId(),
                doc.getOrderNo(),
                doc.getSupplyId(),
                doc.getBuyerId(),
                doc.getSellerId(),
                doc.getCategory(),
                Money.ofCny(doc.getDepositAmount()),
                Money.ofCny(doc.getDealAmount()),
                doc.getStatus(),
                doc.getCreatedAt(),
                doc.getCompletedAt(),
                doc.getPendingConfirmAt(),
                doc.getConfirmedAt(),
                doc.getInProgressAt(),
                doc.getPayChannel(),
                doc.getPaymentId(),
                doc.getDepositExpireAt(),
                doc.getCancelledAt()
        );
    }
}
