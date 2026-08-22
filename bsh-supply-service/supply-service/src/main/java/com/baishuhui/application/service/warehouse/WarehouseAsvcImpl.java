package com.baishuhui.application.service.warehouse;

import com.baishuhui.supply.vo.StockMoveCommand;
import com.baishuhui.supply.vo.WarehouseLocationCommand;
import com.baishuhui.supply.vo.StockMoveDTO;
import com.baishuhui.supply.vo.WarehouseLocationDTO;
import com.baishuhui.supply.vo.WarehouseStockDTO;
import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.response.Result;
import com.baishuhui.common.util.IdUtil;
import com.baishuhui.domain.supply.entity.StockMove;
import com.baishuhui.domain.supply.entity.WarehouseLocation;
import com.baishuhui.domain.supply.entity.WarehouseStock;
import com.baishuhui.domain.supply.repositories.IWarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 仓位、库存与出入库编排。
 *
 * @author wei yz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseAsvcImpl implements IWarehouseAsvc {
    private static final int MOVE_LIMIT = 100;

    private final IWarehouseRepository warehouseRepository;

    /**
     * 仓位列表。
     */
    @Override
    public Result<List<WarehouseLocationDTO>> listLocations(String merchantId) {
        return Result.success(warehouseRepository.listLocations(merchantId).stream()
                .map(this::toLocationDto)
                .collect(Collectors.toList()));
    }

    /**
     * 新建仓位。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<WarehouseLocationDTO> createLocation(WarehouseLocationCommand cmd) {
        WarehouseLocation loc = WarehouseLocation.create(
                IdUtil.nextId(), cmd.getMerchantId(), cmd.getName(), cmd.getRemark());
        warehouseRepository.saveLocation(loc);
        return Result.success(toLocationDto(loc));
    }

    /**
     * 改仓位名。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<WarehouseLocationDTO> updateLocation(String id, WarehouseLocationCommand cmd) {
        WarehouseLocation loc = requireLocation(id, cmd.getMerchantId());
        loc.rename(cmd.getName(), cmd.getRemark());
        warehouseRepository.saveLocation(loc);
        return Result.success(toLocationDto(loc));
    }

    /**
     * 删除空仓位。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<Void> removeLocation(String id, String merchantId) {
        requireLocation(id, merchantId);
        List<WarehouseStock> stocks = warehouseRepository.listStocks(merchantId, id);
        boolean occupied = stocks.stream().anyMatch(s -> s.getQuantity().signum() > 0);
        if (occupied) {
            throw new BusinessException(ErrorCode.INVALID_LOCATION, "仓位仍有库存，不能删除");
        }
        warehouseRepository.removeLocation(id);
        return Result.success();
    }

    /**
     * 库存列表。
     */
    @Override
    public Result<List<WarehouseStockDTO>> listStocks(String merchantId, String locationId) {
        Map<String, String> names = locationNames(merchantId);
        List<WarehouseStock> stocks = warehouseRepository.listStocks(merchantId, locationId);
        return Result.success(stocks.stream()
                .map(s -> toStockDto(s, names.get(s.getLocationId())))
                .collect(Collectors.toList()));
    }

    /**
     * 出入库。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<WarehouseStockDTO> move(StockMoveCommand cmd) {
        WarehouseLocation loc = requireLocation(cmd.getLocationId(), cmd.getMerchantId());
        String dir = cmd.getDirection().trim().toUpperCase();
        if (!StockMove.IN.equals(dir) && !StockMove.OUT.equals(dir)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "方向只能是 IN 或 OUT");
        }
        WarehouseStock stock = warehouseRepository
                .findStock(cmd.getMerchantId(), cmd.getLocationId(), cmd.getCategory(), cmd.getUnit())
                .orElseGet(() -> WarehouseStock.create(
                        IdUtil.nextId(), cmd.getMerchantId(), cmd.getLocationId(),
                        cmd.getCategory(), cmd.getUnit()));
        if (StockMove.IN.equals(dir)) {
            stock.inbound(cmd.getQuantity());
        } else {
            stock.outbound(cmd.getQuantity());
        }
        warehouseRepository.saveStock(stock);
        warehouseRepository.saveMove(new StockMove(
                IdUtil.nextId(),
                cmd.getMerchantId(),
                cmd.getLocationId(),
                stock.getCategory(),
                stock.getUnit(),
                cmd.getQuantity(),
                dir,
                cmd.getRemark(),
                LocalDateTime.now()));
        return Result.success(toStockDto(stock, loc.getName()));
    }

    /**
     * 结单自动出库：按品类（优先同单位）找够量的仓位扣减；无仓或不够则跳过。
     *
     * @param merchantId 卖家 id
     * @param category   品类
     * @param unit       单位，可空
     * @param quantity   出库数量
     * @param orderId    订单 id，写入流水备注
     */
    public void outboundOnComplete(String merchantId, String category, String unit,
                                   BigDecimal quantity, String orderId) {
        if (!StringUtils.hasText(merchantId) || !StringUtils.hasText(category)
                || quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        String cat = category.trim();
        String unitNorm = StringUtils.hasText(unit) ? unit.trim() : null;
        List<WarehouseStock> stocks = warehouseRepository.listStocks(merchantId, null);
        WarehouseStock picked = pickStock(stocks, cat, unitNorm, quantity);
        if (picked == null) {
            log.info("warehouse outbound skip, no stock merchantId={} category={} qty={}",
                    merchantId, cat, quantity);
            return;
        }
        picked.outbound(quantity);
        warehouseRepository.saveStock(picked);
        String remark = StringUtils.hasText(orderId) ? "结单出库 " + orderId.trim() : "结单出库";
        warehouseRepository.saveMove(new StockMove(
                IdUtil.nextId(),
                merchantId,
                picked.getLocationId(),
                picked.getCategory(),
                picked.getUnit(),
                quantity,
                StockMove.OUT,
                remark,
                LocalDateTime.now()));
        log.info("warehouse outbound ok merchantId={} locationId={} category={} qty={} orderId={}",
                merchantId, picked.getLocationId(), cat, quantity, orderId);
    }

    /**
     * 优先同单位且库存足够，否则任意单位足够的仓位。
     */
    private WarehouseStock pickStock(List<WarehouseStock> stocks, String category, String unit, BigDecimal qty) {
        WarehouseStock any = null;
        for (WarehouseStock stock : stocks) {
            if (stock == null || stock.getQuantity() == null || stock.getQuantity().compareTo(qty) < 0) {
                continue;
            }
            if (stock.getCategory() == null || !category.equalsIgnoreCase(stock.getCategory().trim())) {
                continue;
            }
            if (unit != null && unit.equalsIgnoreCase(stock.getUnit())) {
                return stock;
            }
            if (any == null) {
                any = stock;
            }
        }
        return any;
    }

    /**
     * 出入库流水。
     */
    @Override
    public Result<List<StockMoveDTO>> listMoves(String merchantId) {
        Map<String, String> names = locationNames(merchantId);
        return Result.success(warehouseRepository.listMoves(merchantId, MOVE_LIMIT).stream()
                .map(m -> StockMoveDTO.builder()
                        .id(m.getId())
                        .merchantId(m.getMerchantId())
                        .locationId(m.getLocationId())
                        .locationName(names.get(m.getLocationId()))
                        .category(m.getCategory())
                        .unit(m.getUnit())
                        .quantity(m.getQuantity())
                        .direction(m.getDirection())
                        .remark(m.getRemark())
                        .createdAt(m.getCreatedAt())
                        .build())
                .collect(Collectors.toList()));
    }

    private WarehouseLocation requireLocation(String id, String merchantId) {
        WarehouseLocation loc = warehouseRepository.findLocation(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_LOCATION, "仓位不存在"));
        loc.assertOwnedBy(merchantId);
        return loc;
    }

    private Map<String, String> locationNames(String merchantId) {
        return warehouseRepository.listLocations(merchantId).stream()
                .collect(Collectors.toMap(WarehouseLocation::getId, WarehouseLocation::getName, (a, b) -> a));
    }

    private WarehouseLocationDTO toLocationDto(WarehouseLocation loc) {
        return WarehouseLocationDTO.builder()
                .id(loc.getId())
                .merchantId(loc.getMerchantId())
                .name(loc.getName())
                .remark(loc.getRemark())
                .status(loc.getStatus())
                .build();
    }

    private WarehouseStockDTO toStockDto(WarehouseStock stock, String locationName) {
        return WarehouseStockDTO.builder()
                .id(stock.getId())
                .merchantId(stock.getMerchantId())
                .locationId(stock.getLocationId())
                .locationName(locationName)
                .category(stock.getCategory())
                .unit(stock.getUnit())
                .quantity(stock.getQuantity())
                .build();
    }
}
