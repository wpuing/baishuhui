package com.baishuhui.domain.supply.repositories;

import com.baishuhui.domain.supply.entity.StockMove;
import com.baishuhui.domain.supply.entity.WarehouseLocation;
import com.baishuhui.domain.supply.entity.WarehouseStock;

import java.util.List;
import java.util.Optional;

/**
 * 仓位 / 库存 / 出入库流水仓储。
 *
 * @author wei yz
 */
public interface IWarehouseRepository {

    /** 按 id 查仓位。 */
    Optional<WarehouseLocation> findLocation(String id);

    /** 商家仓位列表。 */
    List<WarehouseLocation> listLocations(String merchantId);

    /** 保存仓位。 */
    void saveLocation(WarehouseLocation location);

    /** 删除仓位。 */
    void removeLocation(String id);

    /** 按仓位+品类+单位查库存。 */
    Optional<WarehouseStock> findStock(String merchantId, String locationId, String category, String unit);

    /** 库存列表；locationId 空则查全部。 */
    List<WarehouseStock> listStocks(String merchantId, String locationId);

    /** 保存库存。 */
    void saveStock(WarehouseStock stock);

    /** 保存出入库流水。 */
    void saveMove(StockMove move);

    /** 最近出入库流水。 */
    List<StockMove> listMoves(String merchantId, int limit);
}
