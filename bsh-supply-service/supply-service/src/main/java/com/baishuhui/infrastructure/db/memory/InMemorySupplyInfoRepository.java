package com.baishuhui.infrastructure.db.memory;

import com.baishuhui.domain.supply.entity.SupplyInfo;
import com.baishuhui.domain.supply.repositories.ISupplyInfoRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 供应信息内存仓储（开发/测试）。
 *
 * @author wei yz
 */
@Repository
@Profile("demo")
public class InMemorySupplyInfoRepository implements ISupplyInfoRepository {

    private final Map<String, SupplyInfo> store = new ConcurrentHashMap<>();

    /**
     * 按标识查询。
     */
    @Override

    public Optional<SupplyInfo> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    /**
     * 保存。
     */
    @Override

    public void save(SupplyInfo aggregate) {
        store.put(aggregate.getId(), aggregate);
    }

    /**
     * 删除。
     */
    @Override

    public void remove(String id) {
        store.remove(id);
    }

    /**
     * findByMerchantId。
     */
    @Override

    public List<SupplyInfo> findByMerchantId(String merchantId) {
        return store.values().stream()
                .filter(s -> merchantId.equals(s.getMerchantId()))
                .collect(Collectors.toList());
    }

    /**
     * findPublished。
     */
    @Override
    public List<SupplyInfo> findPublished() {
        return store.values().stream()
                .filter(s -> SupplyInfo.PUBLISHED.equals(s.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * findBrowsable。
     */
    @Override
    public List<SupplyInfo> findBrowsable(int limit) {
        int size = Math.min(Math.max(limit, 1), 500);
        return store.values().stream()
                .filter(s -> !SupplyInfo.DRAFT.equals(s.getStatus())
                        && !SupplyInfo.CANCELLED.equals(s.getStatus()))
                .sorted((a, b) -> {
                    if (a.getPublishTime() == null && b.getPublishTime() == null) {
                        return 0;
                    }
                    if (a.getPublishTime() == null) {
                        return 1;
                    }
                    if (b.getPublishTime() == null) {
                        return -1;
                    }
                    return b.getPublishTime().compareTo(a.getPublishTime());
                })
                .limit(size)
                .collect(Collectors.toList());
    }

    /**
     * findAll。
     */
    @Override
    public List<SupplyInfo> findAll() {
        return List.copyOf(store.values());
    }
}
