package com.baishuhui.application.service.order;

import com.baishuhui.order.vo.CancelDepositCommand;
import com.baishuhui.order.vo.CompleteTradeCommand;
import com.baishuhui.order.vo.ConfirmTradeCommand;
import com.baishuhui.order.vo.PayDepositCommand;
import com.baishuhui.order.vo.PlaceDepositCommand;
import com.baishuhui.order.vo.TradeActionCommand;
import com.baishuhui.order.vo.CategoryRankDTO;
import com.baishuhui.order.vo.TradeOrderDTO;
import com.baishuhui.order.vo.TradePageResultDTO;
import com.baishuhui.order.vo.WarehouseStatsDTO;
import com.baishuhui.client.supply.feign.ISupplyFeignService;
import com.baishuhui.supply.vo.SupplyInfoDTO;
import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.response.Result;
import com.baishuhui.domain.order.entity.TradeOrder;
import com.baishuhui.domain.order.repositories.ITradeOrderRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 应用服务接口（原 IOrderAsvc）。
 *
 * @author wei yz
 */
public interface IOrderAsvc {
    Result<TradeOrderDTO> placeDeposit(PlaceDepositCommand cmd);
    Result<TradeOrderDTO> payDeposit(PayDepositCommand cmd);
    Result<TradeOrderDTO> cancelDeposit(CancelDepositCommand cmd);
    Result<TradeOrderDTO> cancelByOperator(TradeActionCommand cmd);
    int cancelExpiredDeposits();
    Result<TradeOrderDTO> confirm(ConfirmTradeCommand cmd);
    Result<TradeOrderDTO> complete(CompleteTradeCommand cmd);
    Result<TradeOrderDTO> submitConfirm(TradeActionCommand cmd);
    Result<TradeOrderDTO> startTrade(TradeActionCommand cmd);
    Result<TradePageResultDTO<TradeOrderDTO>> pageByBuyer(String buyerId, String status, int pageNum, int pageSize);
    Result<TradePageResultDTO<TradeOrderDTO>> pageBySeller(String sellerId, String status, String category, String keyword,
            String fromDate, String toDate, int pageNum, int pageSize);
    Result<TradePageResultDTO<TradeOrderDTO>> pageWarehouse(String sellerId, String category, String keyword, String fromDate, String toDate, int pageNum, int pageSize);
    Result<TradeOrderDTO> detailBySeller(String orderId, String sellerId);
    Result<TradeOrderDTO> detailByBuyer(String orderId, String buyerId);
    Result<WarehouseStatsDTO> warehouseStats(String sellerId, String category, String keyword, String fromDate, String toDate);
    Result<List<CategoryRankDTO>> categoryRanking(int topN);
}
