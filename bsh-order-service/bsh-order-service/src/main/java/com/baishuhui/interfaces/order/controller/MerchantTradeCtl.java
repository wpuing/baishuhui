package com.baishuhui.interfaces.order.controller;

import com.baishuhui.order.vo.CompleteTradeCommand;
import com.baishuhui.order.vo.ConfirmTradeCommand;
import com.baishuhui.order.vo.TradeActionCommand;
import com.baishuhui.order.vo.TradeOrderDTO;
import com.baishuhui.order.vo.TradePageResultDTO;
import com.baishuhui.order.vo.WarehouseStatsDTO;
import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.response.Result;
import com.baishuhui.application.service.order.IOrderAsvc;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家侧交易 REST 接口。
 *
 * @author wei yz
 */
@RestController
@RequestMapping("/api/merchant")
@RequiredArgsConstructor
@Slf4j
public class MerchantTradeCtl {
    private final IOrderAsvc orderAsvc;

    /**
     * 卖家交易分页列表（已拍商品）。
     */
    @GetMapping("/trades")
    public Result<TradePageResultDTO<TradeOrderDTO>> page(
            @RequestParam String sellerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        log.info("page invoked");
        return orderAsvc.pageBySeller(sellerId, status, category, keyword, fromDate, toDate, pageNum, pageSize);
    }

    /**
     * 卖家交易详情。
     */
    @GetMapping("/trades/{orderId}")
    public Result<TradeOrderDTO> detail(
            @PathVariable String orderId,
            @RequestParam String sellerId) {
        log.info("detail invoked");
        return orderAsvc.detailBySeller(orderId, sellerId);
    }

    /**
     * 我的仓库：已完成交易分页。
     */
    @GetMapping("/warehouse")
    public Result<TradePageResultDTO<TradeOrderDTO>> warehouse(
            @RequestParam String sellerId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        log.info("warehouse invoked");
        return orderAsvc.pageWarehouse(sellerId, category, keyword, fromDate, toDate, pageNum, pageSize);
    }

    /**
     * 我的仓库：统计图表。
     */
    @GetMapping("/warehouse/stats")
    public Result<WarehouseStatsDTO> warehouseStats(
            @RequestParam String sellerId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {
        log.info("warehouseStats invoked");
        return orderAsvc.warehouseStats(sellerId, category, keyword, fromDate, toDate);
    }

    @PostMapping("/trades/submit-confirm")
    public Result<TradeOrderDTO> submitConfirm(@Valid @RequestBody TradeActionCommand command) {
        log.info("submitConfirm invoked");
        return orderAsvc.submitConfirm(command);
    }

    @PostMapping({"/trades/confirm", "/supplies/confirm"})
    public Result<TradeOrderDTO> confirm(@Valid @RequestBody ConfirmTradeCommand command) {
        log.info("confirm invoked");
        return orderAsvc.confirm(command);
    }

    @PostMapping("/trades/start")
    public Result<TradeOrderDTO> start(@Valid @RequestBody TradeActionCommand command) {
        log.info("start invoked");
        return orderAsvc.startTrade(command);
    }

    @PostMapping({"/trades/complete", "/supplies/complete"})
    public Result<TradeOrderDTO> complete(
            @Valid @RequestBody CompleteTradeCommand command,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        log.info("complete orderId={} operatorId={}", command.getOrderId(), command.getOperatorId());
        assertLoggedOperator(userId, command.getOperatorId());
        return orderAsvc.complete(command);
    }

    /**
     * 已拍下 / 待确认：卖家规则允许的取消（退定金）。
     */
    @PostMapping("/trades/cancel")
    public Result<TradeOrderDTO> cancel(
            @Valid @RequestBody TradeActionCommand command,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        log.info("cancel orderId={} operatorId={}", command.getOrderId(), command.getOperatorId());
        assertOperator(userId, command.getOperatorId());
        return orderAsvc.cancelByOperator(command);
    }

    private static void assertOperator(String userId, String operatorId) {
        if (userId != null && !userId.isBlank() && !userId.equals(operatorId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "操作人与登录用户不一致");
        }
    }

    private static void assertLoggedOperator(String userId, String operatorId) {
        if (userId == null || userId.isBlank()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        if (!userId.equals(operatorId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "操作人与登录用户不一致");
        }
    }
}
