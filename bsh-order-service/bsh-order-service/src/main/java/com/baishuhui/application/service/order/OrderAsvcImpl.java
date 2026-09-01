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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
 * 订单应用服务编排入口。
 *
 * @author wei yz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderAsvcImpl implements IOrderAsvc {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;
    private static final int STATS_LIMIT = 500;
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final PlaceDepositUseCase placeDepositUseCase;
    private final PayDepositUseCase payDepositUseCase;
    private final CancelDepositUseCase cancelDepositUseCase;
    private final ConfirmTradeUseCase confirmTradeUseCase;
    private final CompleteTradeUseCase completeTradeUseCase;
    private final SubmitConfirmUseCase submitConfirmUseCase;
    private final StartTradeUseCase startTradeUseCase;
    private final ITradeOrderRepository tradeOrderRepository;
    private final ISupplyFeignService supplyFeignClient;

    @Override
    public Result<TradeOrderDTO> placeDeposit(PlaceDepositCommand cmd) {
        return placeDepositUseCase.execute(cmd);
    }

    /**
     * 支付定金（账户扣款 + 锁定供应）。
     */
    @Override
    public Result<TradeOrderDTO> payDeposit(PayDepositCommand cmd) {
        return payDepositUseCase.execute(cmd);
    }

    /**
     * 取消预定 / 取消已付定金订单。
     */
    @Override
    public Result<TradeOrderDTO> cancelDeposit(CancelDepositCommand cmd) {
        return cancelDepositUseCase.execute(cmd);
    }

    /**
     * 已拍后规则允许的取消（买家或卖家）。
     */
    @Override
    public Result<TradeOrderDTO> cancelByOperator(TradeActionCommand cmd) {
        return cancelDepositUseCase.executeByOperator(cmd);
    }

    /**
     * 定时任务入口：取消超时未付定金订单。
     */
    @Override
    public int cancelExpiredDeposits() {
        return cancelDepositUseCase.cancelExpired();
    }

    @Override
    public Result<TradeOrderDTO> confirm(ConfirmTradeCommand cmd) {
        return confirmTradeUseCase.execute(cmd);
    }

    @Override
    public Result<TradeOrderDTO> complete(CompleteTradeCommand cmd) {
        return completeTradeUseCase.execute(cmd);
    }

    @Override
    public Result<TradeOrderDTO> submitConfirm(TradeActionCommand cmd) {
        return submitConfirmUseCase.execute(cmd);
    }

    @Override
    public Result<TradeOrderDTO> startTrade(TradeActionCommand cmd) {
        return startTradeUseCase.execute(cmd);
    }

    /**
     * 买家交易分页。
     */
    @Override
    public Result<TradePageResultDTO<TradeOrderDTO>> pageByBuyer(String buyerId, String status, int pageNum, int pageSize) {
        int num = Math.max(pageNum, 1);
        int size = pageSize <= 0 ? DEFAULT_PAGE_SIZE : Math.min(pageSize, MAX_PAGE_SIZE);
        String st = StringUtils.hasText(status) ? TradeOrder.normalizeStatus(status) : null;
        long total = tradeOrderRepository.countByBuyer(buyerId, st);
        List<TradeOrder> orders = tradeOrderRepository.pageByBuyer(buyerId, st, num, size);
        return Result.success(TradePageResultDTO.of(toEnrichedDtos(orders), total, num, size));
    }

    /**
     * 卖家交易分页（已拍商品）。
     */
    public Result<TradePageResultDTO<TradeOrderDTO>> pageBySeller(
            String sellerId, String status, String category, String keyword,
            String fromDate, String toDate, int pageNum, int pageSize) {
        int num = Math.max(pageNum, 1);
        int size = pageSize <= 0 ? DEFAULT_PAGE_SIZE : Math.min(pageSize, MAX_PAGE_SIZE);
        String st = StringUtils.hasText(status) ? TradeOrder.normalizeStatus(status) : null;
        LocalDateTime from = parseFrom(fromDate);
        LocalDateTime to = parseToExclusive(toDate);
        long total = tradeOrderRepository.countBySeller(sellerId, st, blankToNull(category), blankToNull(keyword), from, to);
        List<TradeOrder> orders = tradeOrderRepository.pageBySeller(
                sellerId, st, blankToNull(category), blankToNull(keyword), from, to, num, size);
        return Result.success(TradePageResultDTO.of(toEnrichedDtos(orders), total, num, size));
    }

    /**
     * 仓库：已完成交易分页。
     */
    public Result<TradePageResultDTO<TradeOrderDTO>> pageWarehouse(
            String sellerId, String category, String keyword, String fromDate, String toDate, int pageNum, int pageSize) {
        return pageBySeller(sellerId, TradeOrder.COMPLETED, category, keyword, fromDate, toDate, pageNum, pageSize);
    }

    /**
     * 卖家交易详情（校验归属）。
     */
    @Override
    public Result<TradeOrderDTO> detailBySeller(String orderId, String sellerId) {
        // 字符串非空才继续处理
        if (!StringUtils.hasText(orderId) || !StringUtils.hasText(sellerId)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "订单或卖家不能为空");
        }
        TradeOrder order = tradeOrderRepository.findById(orderId.trim())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND, "交易订单不存在"));
        // 字段相等性校验
        if (!sellerId.trim().equals(order.getSellerId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权查看该交易");
        }
        return Result.success(TradeAssembler.toDTO(order, loadSupply(order.getSupplyId(), new HashMap<>())));
    }

    /**
     * 买家交易详情（校验归属）。
     */
    @Override
    public Result<TradeOrderDTO> detailByBuyer(String orderId, String buyerId) {
        // 字符串非空才继续处理
        if (!StringUtils.hasText(orderId) || !StringUtils.hasText(buyerId)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "订单或买家不能为空");
        }
        TradeOrder order = tradeOrderRepository.findById(orderId.trim())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND, "交易订单不存在"));
        // 字段相等性校验
        if (!buyerId.trim().equals(order.getBuyerId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权查看该交易");
        }
        return Result.success(TradeAssembler.toDTO(order, loadSupply(order.getSupplyId(), new HashMap<>())));
    }

    /**
     * 仓库图表统计。
     */
    public Result<WarehouseStatsDTO> warehouseStats(
            String sellerId, String category, String keyword, String fromDate, String toDate) {
        LocalDateTime from = parseFrom(fromDate);
        LocalDateTime to = parseToExclusive(toDate);
        List<TradeOrder> orders = tradeOrderRepository.listBySeller(
                sellerId, TradeOrder.COMPLETED, blankToNull(category), blankToNull(keyword), from, to, STATS_LIMIT);

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal dayAmount = BigDecimal.ZERO;
        BigDecimal monthAmount = BigDecimal.ZERO;
        BigDecimal yearAmount = BigDecimal.ZERO;
        LocalDate today = LocalDate.now();
        Map<String, WarehouseStatsDTO.NamedAmountDTO> byCat = new HashMap<>();
        Map<String, WarehouseStatsDTO.NamedAmountDTO> byMonth = new LinkedHashMap<>();

        // 遍历集合逐项处理
        for (TradeOrder order : orders) {
            BigDecimal amount = order.getDealAmount() == null ? BigDecimal.ZERO : order.getDealAmount().amount();
            totalAmount = totalAmount.add(amount);
            LocalDateTime markTime = order.getCompletedAt() != null ? order.getCompletedAt() : order.getCreatedAt();
            // 空值分支判断
            if (markTime != null) {
                LocalDate markDate = markTime.toLocalDate();
                // 字段相等性校验
                if (markDate.equals(today)) {
                    dayAmount = dayAmount.add(amount);
                }
                // 业务条件分支
                if (markDate.getYear() == today.getYear() && markDate.getMonth() == today.getMonth()) {
                    monthAmount = monthAmount.add(amount);
                }
                // 业务条件分支
                if (markDate.getYear() == today.getYear()) {
                    yearAmount = yearAmount.add(amount);
                }
            }
            String cat = order.getCategory() == null ? "未分类" : order.getCategory();
            byCat.merge(cat, WarehouseStatsDTO.NamedAmountDTO.builder().name(cat).count(1).amount(amount).build(),
                    (a, b) -> WarehouseStatsDTO.NamedAmountDTO.builder()
                            .name(cat)
                            .count(a.getCount() + b.getCount())
                            .amount(a.getAmount().add(b.getAmount()))
                            .build());
            String month = order.getCompletedAt() != null
                    ? order.getCompletedAt().format(MONTH_FMT)
                    : (order.getCreatedAt() != null ? order.getCreatedAt().format(MONTH_FMT) : "未知");
            byMonth.merge(month, WarehouseStatsDTO.NamedAmountDTO.builder().name(month).count(1).amount(amount).build(),
                    (a, b) -> WarehouseStatsDTO.NamedAmountDTO.builder()
                            .name(month)
                            .count(a.getCount() + b.getCount())
                            .amount(a.getAmount().add(b.getAmount()))
                            .build());
        }

        List<WarehouseStatsDTO.NamedAmountDTO> catList = byCat.values().stream()
                .sorted(Comparator.comparing(WarehouseStatsDTO.NamedAmountDTO::getAmount).reversed())
                .collect(Collectors.toList());
        List<WarehouseStatsDTO.NamedAmountDTO> monthList = byMonth.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        return Result.success(WarehouseStatsDTO.builder()
                .totalCount(orders.size())
                .totalAmount(totalAmount)
                .dayAmount(dayAmount)
                .monthAmount(monthAmount)
                .yearAmount(yearAmount)
                .byCategory(catList)
                .byMonth(monthList)
                .build());
    }

    @Override
    public Result<List<CategoryRankDTO>> categoryRanking(int topN) {
        List<TradeOrder> completed = tradeOrderRepository.findCompleted();
        Map<String, List<TradeOrder>> grouped = completed.stream()
                .collect(Collectors.groupingBy(TradeOrder::getCategory));
        List<CategoryRankDTO> ranks = grouped.entrySet().stream()
                .map(e -> {
                    BigDecimal total = e.getValue().stream()
                            .map(o -> o.getDealAmount().amount())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return CategoryRankDTO.builder()
                            .category(e.getKey())
                            .orderCount(e.getValue().size())
                            .totalAmount(total)
                            .build();
                })
                .sorted(Comparator.comparing(CategoryRankDTO::getTotalAmount).reversed())
                .limit(Math.max(topN, 1))
                .collect(Collectors.toList());
        // 循环处理
        for (int i = 0; i < ranks.size(); i++) {
            ranks.get(i).setRank(i + 1);
        }
        return Result.success(ranks);
    }

    private List<TradeOrderDTO> toEnrichedDtos(List<TradeOrder> orders) {
        Map<String, SupplyInfoDTO> cache = new HashMap<>();
        return orders.stream().map(o -> TradeAssembler.toDTO(o, loadSupply(o.getSupplyId(), cache))).collect(Collectors.toList());
    }

    private SupplyInfoDTO loadSupply(String supplyId, Map<String, SupplyInfoDTO> cache) {
        // 字符串非空才继续处理
        if (!StringUtils.hasText(supplyId)) {
            return null;
        }
        // 业务条件分支
        if (cache.containsKey(supplyId)) {
            return cache.get(supplyId);
        }
        try {
            Result<SupplyInfoDTO> res = supplyFeignClient.detail(supplyId);
            SupplyInfoDTO dto = (res != null && ErrorCode.OK.equals(res.getCode())) ? res.getData() : null;
            cache.put(supplyId, dto);
            return dto;
        } catch (Exception e) {
            log.warn("enrich supply fail supplyId={}", supplyId);
            cache.put(supplyId, null);
            return null;
        }
    }

    private String blankToNull(String v) {
        return StringUtils.hasText(v) ? v.trim() : null;
    }

    private LocalDateTime parseFrom(String date) {
        // 字符串非空才继续处理
        if (!StringUtils.hasText(date)) {
            return null;
        }
        return LocalDate.parse(date.trim()).atStartOfDay();
    }

    private LocalDateTime parseToExclusive(String date) {
        // 字符串非空才继续处理
        if (!StringUtils.hasText(date)) {
            return null;
        }
        return LocalDate.parse(date.trim()).plusDays(1).atStartOfDay();
    }
}
