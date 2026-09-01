package com.baishuhui.domain.price.repositories;

import com.baishuhui.common.ddd.IRepository;
import com.baishuhui.domain.price.entity.MarketPrice;

import com.baishuhui.domain.price.entity.vo.PriceSnapshot;

import java.util.List;
import java.util.Optional;

/**
 * 行情仓储接口（领域层，禁止技术注解）。
 *
 * @author wei yz
 */
public interface IMarketPriceRepository extends IRepository<MarketPrice, String> {

    Optional<MarketPrice> findBySku(String sku);

    /**
     * 按 SKU 倒序历史快照，最多 {@code limit} 条。
     *
     * @param sku   品类 / SKU
     * @param limit 条数
     * @return 历史快照，新在前
     */
    List<PriceSnapshot> listHistory(String sku, int limit);
}
