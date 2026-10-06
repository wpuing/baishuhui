package com.baishuhui.infrastructure.db.memory;

import com.baishuhui.domain.supply.entity.SupplyFavorite;
import com.baishuhui.domain.supply.repositories.ISupplyFavoriteRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 供应收藏内存实现（demo）。
 *
 * @author wei yz
 */
@Repository
@Profile("demo")
public class InMemorySupplyFavoriteRepository implements ISupplyFavoriteRepository {

    private final Map<String, SupplyFavorite> store = new ConcurrentHashMap<>();

    private static String key(String userId, String supplyId) {
        return userId + ":" + supplyId;
    }

    @Override
    public void save(SupplyFavorite favorite) {
        store.put(key(favorite.getUserId(), favorite.getSupplyId()), favorite);
    }

    @Override
    public void remove(String userId, String supplyId) {
        store.remove(key(userId, supplyId));
    }

    @Override
    public Optional<SupplyFavorite> find(String userId, String supplyId) {
        return Optional.ofNullable(store.get(key(userId, supplyId)));
    }

    @Override
    public List<SupplyFavorite> listByUser(String userId, int limit) {
        int size = Math.min(Math.max(limit, 1), 200);
        return store.values().stream()
                .filter(f -> userId.equals(f.getUserId()))
                .sorted(Comparator.comparing(SupplyFavorite::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    public boolean exists(String userId, String supplyId) {
        return store.containsKey(key(userId, supplyId));
    }
}
