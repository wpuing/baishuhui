package com.baishuhui.domain.supply.repositories;

import com.baishuhui.domain.supply.entity.SupplyFavorite;

import java.util.List;
import java.util.Optional;

/**
 * 供应收藏仓储。
 *
 * @author wei yz
 */
public interface ISupplyFavoriteRepository {

    void save(SupplyFavorite favorite);

    void remove(String userId, String supplyId);

    Optional<SupplyFavorite> find(String userId, String supplyId);

    List<SupplyFavorite> listByUser(String userId, int limit);

    boolean exists(String userId, String supplyId);
}
