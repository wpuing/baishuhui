package com.baishuhui.interfaces.order.controller;

import com.baishuhui.order.vo.CancelDepositCommand;
import com.baishuhui.order.vo.CompleteTradeCommand;
import com.baishuhui.order.vo.ConfirmTradeCommand;
import com.baishuhui.order.vo.PayDepositCommand;
import com.baishuhui.order.vo.PlaceDepositCommand;
import com.baishuhui.order.vo.TradeActionCommand;
import com.baishuhui.order.vo.TradeOrderDTO;
import com.baishuhui.order.vo.TradePageResultDTO;
import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.response.Result;
import com.baishuhui.application.service.order.IOrderAsvc;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消费者侧交易 REST 接口。
 *
 * @author wei yz
 */
@RestController
@RequestMapping("/api/consumer")
@RequiredArgsConstructor
public class ConsumerTradeCtl {
    private final IOrderAsvc orderAsvc;

    /**
     * placeDeposit。
     */
    @PostMapping("/deposits")
    @SentinelResource("placeDeposit")
    public Result<TradeOrderDTO> placeDeposit(@Valid @RequestBody PlaceDepositCommand command) {
        return orderAsvc.placeDeposit(command);
    }

    /**
     * 买家交易分页。
     */
    @GetMapping("/trades")
    public Result<TradePageResultDTO<TradeOrderDTO>> page(
            @RequestParam String buyerId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return orderAsvc.pageByBuyer(buyerId, status, pageNum, pageSize);
    }

    /**
     * 卖家（农户）卖出的订单分页。
     */
    @GetMapping("/sold-trades")
    public Result<TradePageResultDTO<TradeOrderDTO>> pageSold(
            @RequestParam String sellerId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        assertLoggedOperator(userId, sellerId);
        return orderAsvc.pageBySeller(sellerId, status, null, null, null, null, pageNum, pageSize);
    }

    /**
     * 买家交易详情。
     */
    @GetMapping("/trades/{orderId}")
    public Result<TradeOrderDTO> detail(
            @PathVariable("orderId") String orderId,
            @RequestParam String buyerId) {
        return orderAsvc.detailByBuyer(orderId, buyerId);
    }

    /**
     * 卖家（农户）交易详情，含流转时间轴。
     */
    @GetMapping("/sold-trades/{orderId}")
    public Result<TradeOrderDTO> soldDetail(
            @PathVariable("orderId") String orderId,
            @RequestParam String sellerId,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        assertLoggedOperator(userId, sellerId);
        return orderAsvc.detailBySeller(orderId, sellerId);
    }

    /**
     * 支付定金（选渠道扣款）。
     */
    @PostMapping("/trades/pay-deposit")
    @SentinelResource("payDeposit")
    public Result<TradeOrderDTO> payDeposit(@Valid @RequestBody PayDepositCommand command) {
        return orderAsvc.payDeposit(command);
    }

    /**
     * 取消预定 / 取消已付定金订单。
     */
    @PostMapping("/trades/cancel-deposit")
    public Result<TradeOrderDTO> cancelDeposit(@Valid @RequestBody CancelDepositCommand command) {
        return orderAsvc.cancelDeposit(command);
    }

    /**
     * 已拍下 / 待确认：买卖双方规则允许的取消。
     */
    @PostMapping("/trades/cancel")
    public Result<TradeOrderDTO> cancel(
            @Valid @RequestBody TradeActionCommand command,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId != null && !userId.isBlank() && !userId.equals(command.getOperatorId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "操作人与登录用户不一致");
        }
        return orderAsvc.cancelByOperator(command);
    }

    /**
     * 提交确认。
     */
    @PostMapping("/trades/submit-confirm")
    public Result<TradeOrderDTO> submitConfirm(@Valid @RequestBody TradeActionCommand command) {
        return orderAsvc.submitConfirm(command);
    }

    /**
     * confirm。
     */
    @PostMapping("/trades/confirm")
    public Result<TradeOrderDTO> confirm(@Valid @RequestBody ConfirmTradeCommand command) {
        return orderAsvc.confirm(command);
    }

    /**
     * 开始线下交易。
     */
    @PostMapping("/trades/start")
    public Result<TradeOrderDTO> start(@Valid @RequestBody TradeActionCommand command) {
        return orderAsvc.startTrade(command);
    }

    /**
     * complete。
     */
    @PostMapping("/trades/complete")
    public Result<TradeOrderDTO> complete(
            @Valid @RequestBody CompleteTradeCommand command,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        assertLoggedOperator(userId, command.getOperatorId());
        return orderAsvc.complete(command);
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
