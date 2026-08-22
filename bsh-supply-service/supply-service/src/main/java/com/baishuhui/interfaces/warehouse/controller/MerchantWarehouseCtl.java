package com.baishuhui.interfaces.warehouse.controller;

import com.baishuhui.supply.vo.StockMoveCommand;
import com.baishuhui.supply.vo.WarehouseLocationCommand;
import com.baishuhui.supply.vo.StockMoveDTO;
import com.baishuhui.supply.vo.WarehouseLocationDTO;
import com.baishuhui.supply.vo.WarehouseStockDTO;
import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.response.Result;
import com.baishuhui.application.service.warehouse.IWarehouseAsvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商家仓库：仓位、库存、出入库。
 *
 * @author wei yz
 */
@Tag(name = "商家仓库")
@RestController
@RequestMapping("/api/merchant/warehouse")
@RequiredArgsConstructor
public class MerchantWarehouseCtl {
    private final IWarehouseAsvc warehouseAsvc;

    /**
     * 仓位列表。
     */
    @Operation(summary = "仓位列表")
    @GetMapping("/locations")
    public Result<List<WarehouseLocationDTO>> locations(
            @RequestParam String merchantId,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        assertMerchant(userId, merchantId);
        return warehouseAsvc.listLocations(merchantId);
    }

    /**
     * 新建仓位。
     */
    @Operation(summary = "新建仓位")
    @PostMapping("/locations")
    public Result<WarehouseLocationDTO> createLocation(
            @Valid @RequestBody WarehouseLocationCommand command,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        assertMerchant(userId, command.getMerchantId());
        return warehouseAsvc.createLocation(command);
    }

    /**
     * 改仓位。
     */
    @Operation(summary = "改仓位")
    @PutMapping("/locations/{id}")
    public Result<WarehouseLocationDTO> updateLocation(
            @PathVariable String id,
            @Valid @RequestBody WarehouseLocationCommand command,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        assertMerchant(userId, command.getMerchantId());
        return warehouseAsvc.updateLocation(id, command);
    }

    /**
     * 删除空仓位。
     */
    @Operation(summary = "删除空仓位")
    @DeleteMapping("/locations/{id}")
    public Result<Void> removeLocation(
            @PathVariable String id,
            @RequestParam String merchantId,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        assertMerchant(userId, merchantId);
        return warehouseAsvc.removeLocation(id, merchantId);
    }

    /**
     * 库存列表。
     */
    @Operation(summary = "库存列表")
    @GetMapping("/stocks")
    public Result<List<WarehouseStockDTO>> stocks(
            @RequestParam String merchantId,
            @RequestParam(required = false) String locationId,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        assertMerchant(userId, merchantId);
        return warehouseAsvc.listStocks(merchantId, locationId);
    }

    /**
     * 出入库。
     */
    @Operation(summary = "出入库")
    @PostMapping("/moves")
    public Result<WarehouseStockDTO> move(
            @Valid @RequestBody StockMoveCommand command,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        assertMerchant(userId, command.getMerchantId());
        return warehouseAsvc.move(command);
    }

    /**
     * 出入库流水。
     */
    @Operation(summary = "出入库流水")
    @GetMapping("/moves")
    public Result<List<StockMoveDTO>> moves(
            @RequestParam String merchantId,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        assertMerchant(userId, merchantId);
        return warehouseAsvc.listMoves(merchantId);
    }

    private static void assertMerchant(String userId, String merchantId) {
        if (userId == null || userId.isBlank()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        if (merchantId == null || merchantId.isBlank() || !userId.equals(merchantId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能操作自己的仓库");
        }
    }
}
