package com.baishuhui.infrastructure.db.memory;

import com.baishuhui.domain.order.entity.TradeOrder;
import com.baishuhui.domain.order.repositories.ITradeOrderRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 交易订单内存仓储（开发/测试）。
 *
 * @author wei yz
 */
@Repository
@Profile("demo")
public class InMemoryTradeOrderRepository implements ITradeOrderRepository {

    private final Map<String, TradeOrder> store = new ConcurrentHashMap<>();

    @Override
    public Optional<TradeOrder> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void save(TradeOrder aggregate) {
        store.put(aggregate.getId(), aggregate);
    }

    @Override
    public void remove(String id) {
        store.remove(id);
    }

    @Override
    public List<TradeOrder> findCompleted() {
        return store.values().stream()
                .filter(o -> TradeOrder.COMPLETED.equals(o.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TradeOrder> findBySupplyId(String supplyId) {
        return store.values().stream()
                .filter(o -> supplyId.equals(o.getSupplyId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TradeOrder> findExpiredDepositPending(LocalDateTime now) {
        return store.values().stream()
                .filter(o -> TradeOrder.DEPOSIT_PENDING.equals(o.getStatus()))
                .filter(o -> o.depositExpired(now))
                .limit(200)
                .collect(Collectors.toList());
    }

    @Override
    public boolean tryCancelExpiredPending(String orderId, LocalDateTime cancelledAt) {
        TradeOrder current = store.get(orderId);
        if (current == null || !TradeOrder.DEPOSIT_PENDING.equals(current.getStatus())) {
            return false;
        }
        current.cancelPending();
        store.put(orderId, current);
        return true;
    }

    @Override
    public List<TradeOrder> pageByBuyer(String buyerId, String status, int pageNum, int pageSize) {
        return page(filter("buyer", buyerId, status, null, null, null, null), pageNum, pageSize);
    }

    @Override
    public long countByBuyer(String buyerId, String status) {
        return filter("buyer", buyerId, status, null, null, null, null).count();
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
        return page(filter("seller", sellerId, status, category, keyword, fromInclusive, toExclusive), pageNum, pageSize);
    }

    @Override
    public long countBySeller(
            String sellerId,
            String status,
            String category,
            String keyword,
            LocalDateTime fromInclusive,
            LocalDateTime toExclusive) {
        return filter("seller", sellerId, status, category, keyword, fromInclusive, toExclusive).count();
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
        return filter("seller", sellerId, status, category, keyword, fromInclusive, toExclusive)
                .sorted(Comparator.comparing(TradeOrder::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(Math.max(limit, 1))
                .collect(Collectors.toList());
    }

    private Stream<TradeOrder> filter(
            String party,
            String partyId,
            String status,
            String category,
            String keyword,
            LocalDateTime fromInclusive,
            LocalDateTime toExclusive) {
        return store.values().stream()
                .filter(o -> "buyer".equals(party) ? partyId.equals(o.getBuyerId()) : partyId.equals(o.getSellerId()))
                .filter(o -> matchStatus(o, status))
                .filter(o -> !StringUtils.hasText(category) || category.equals(o.getCategory()))
                .filter(o -> matchKeyword(o, keyword))
                .filter(o -> matchTime(o, fromInclusive, toExclusive));
    }

    private boolean matchStatus(TradeOrder o, String status) {
        if (!StringUtils.hasText(status)) {
            return true;
        }
        return TradeOrder.normalizeStatus(status).equals(TradeOrder.normalizeStatus(o.getStatus()));
    }

    private boolean matchKeyword(TradeOrder o, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return true;
        }
        String kw = keyword.trim().toLowerCase(Locale.ROOT);
        return (o.getOrderNo() != null && o.getOrderNo().toLowerCase(Locale.ROOT).contains(kw))
                || (o.getCategory() != null && o.getCategory().toLowerCase(Locale.ROOT).contains(kw))
                || (o.getSupplyId() != null && o.getSupplyId().toLowerCase(Locale.ROOT).contains(kw));
    }

    private boolean matchTime(TradeOrder o, LocalDateTime fromInclusive, LocalDateTime toExclusive) {
        LocalDateTime t = o.getCreatedAt();
        if (t == null) {
            return fromInclusive == null && toExclusive == null;
        }
        if (fromInclusive != null && t.isBefore(fromInclusive)) {
            return false;
        }
        if (toExclusive != null && !t.isBefore(toExclusive)) {
            return false;
        }
        return true;
    }

    private List<TradeOrder> page(Stream<TradeOrder> stream, int pageNum, int pageSize) {
        int num = Math.max(pageNum, 1);
        int size = Math.max(pageSize, 1);
        return stream
                .sorted(Comparator.comparing(TradeOrder::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .skip((long) (num - 1) * size)
                .limit(size)
                .collect(Collectors.toList());
    }
}
