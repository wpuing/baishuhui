package com.baishuhui.infrastructure.db.memory;

import com.baishuhui.domain.supply.entity.StockMove;
import com.baishuhui.domain.supply.entity.WarehouseLocation;
import com.baishuhui.domain.supply.entity.WarehouseStock;
import com.baishuhui.domain.supply.repositories.IWarehouseRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 仓库内存仓储（demo）。
 *
 * @author wei yz
 */
@Repository
@Profile("demo")
public class InMemoryWarehouseRepository implements IWarehouseRepository {
    private final Map<String, WarehouseLocation> locations = new ConcurrentHashMap<>();
    private final Map<String, WarehouseStock> stocks = new ConcurrentHashMap<>();
    private final Map<String, StockMove> moves = new ConcurrentHashMap<>();

    @Override
    public Optional<WarehouseLocation> findLocation(String id) {
        return Optional.ofNullable(locations.get(id));
    }

    @Override
    public List<WarehouseLocation> listLocations(String merchantId) {
        return locations.values().stream()
                .filter(l -> merchantId.equals(l.getMerchantId()))
                .collect(Collectors.toList());
    }

    @Override
    public void saveLocation(WarehouseLocation location) {
        locations.put(location.getId(), location);
    }

    @Override
    public void removeLocation(String id) {
        locations.remove(id);
    }

    @Override
    public Optional<WarehouseStock> findStock(String merchantId, String locationId, String category, String unit) {
        return stocks.values().stream()
                .filter(s -> merchantId.equals(s.getMerchantId())
                        && locationId.equals(s.getLocationId())
                        && category.equals(s.getCategory())
                        && unit.equals(s.getUnit()))
                .findFirst();
    }

    @Override
    public List<WarehouseStock> listStocks(String merchantId, String locationId) {
        return stocks.values().stream()
                .filter(s -> merchantId.equals(s.getMerchantId()))
                .filter(s -> !StringUtils.hasText(locationId) || locationId.equals(s.getLocationId()))
                .collect(Collectors.toList());
    }

    @Override
    public void saveStock(WarehouseStock stock) {
        stocks.put(stock.getId(), stock);
    }

    @Override
    public void saveMove(StockMove move) {
        moves.put(move.getId(), move);
    }

    @Override
    public List<StockMove> listMoves(String merchantId, int limit) {
        return moves.values().stream()
                .filter(m -> merchantId.equals(m.getMerchantId()))
                .sorted(Comparator.comparing(StockMove::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(Math.max(1, limit))
                .collect(Collectors.toList());
    }
}
