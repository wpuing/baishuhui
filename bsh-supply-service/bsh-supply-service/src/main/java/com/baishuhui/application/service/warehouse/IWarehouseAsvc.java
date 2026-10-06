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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 应用服务接口（原 IWarehouseAsvc）。
 *
 * @author wei yz
 */
public interface IWarehouseAsvc {
    Result<List<WarehouseLocationDTO>> listLocations(String merchantId);
    Result<WarehouseLocationDTO> createLocation(WarehouseLocationCommand cmd);
    Result<WarehouseLocationDTO> updateLocation(String id, WarehouseLocationCommand cmd);
    Result<Void> removeLocation(String id, String merchantId);
    Result<List<WarehouseStockDTO>> listStocks(String merchantId, String locationId);
    Result<WarehouseStockDTO> move(StockMoveCommand cmd);
    void outboundOnComplete(String merchantId, String category, String unit,
                                   BigDecimal quantity, String orderId);
    Result<List<StockMoveDTO>> listMoves(String merchantId);
}
