package com.baishuhui.infrastructure.db.memory;

import com.baishuhui.domain.price.entity.MarketPrice;
import com.baishuhui.domain.price.entity.vo.PriceSnapshot;
import com.baishuhui.domain.price.repositories.IMarketPriceRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * demo / standalone 内存行情仓储。
 *
 * @author wei yz
 */
@Repository
@Profile("demo | standalone")
public class InMemoryMarketPriceRepository implements IMarketPriceRepository {

    private final Map<String, List<PriceSnapshot>> history = new ConcurrentHashMap<>(16);
    private final Map<String, MarketPrice> latest = new ConcurrentHashMap<>(16);

    @Override
    public Optional<MarketPrice> findById(String id) {
        return findBySku(id);
    }

    @Override
    public void save(MarketPrice aggregate) {
        if (aggregate == null || !StringUtils.hasText(aggregate.getSku()) || aggregate.getLatest() == null) {
            return;
        }
        String sku = aggregate.getSku();
        latest.put(sku, aggregate);
        history.computeIfAbsent(sku, key -> Collections.synchronizedList(new ArrayList<>(8)))
                .add(0, aggregate.getLatest());
    }

    @Override
    public void remove(String id) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        latest.remove(id);
        history.remove(id);
    }

    @Override
    public Optional<MarketPrice> findBySku(String sku) {
        if (!StringUtils.hasText(sku)) {
            return Optional.empty();
        }
        return Optional.ofNullable(latest.get(sku.trim()));
    }

    @Override
    public List<PriceSnapshot> listHistory(String sku, int limit) {
        if (!StringUtils.hasText(sku)) {
            return List.of();
        }
        List<PriceSnapshot> rows = history.get(sku.trim());
        if (rows == null || rows.isEmpty()) {
            return List.of();
        }
        int size = limit <= 0 ? 50 : Math.min(limit, rows.size());
        return List.copyOf(rows.subList(0, size));
    }
}
