package com.baishuhui.interfaces.order.controller;

import com.baishuhui.application.service.order.IOrderAsvc;
import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.response.Result;
import com.baishuhui.order.vo.AdminForceCancelCommand;
import com.baishuhui.order.vo.TradeActionCommand;
import com.baishuhui.order.vo.TradeOrderDTO;
import com.baishuhui.order.vo.TradePageResultDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Locale;

/**
 * 运营端交易订单：列表、详情、强制取消。网关 + 本服务双重校验 ADMIN/SUPER_ADMIN。
 *
 * @author wei yz
 */
@Tag(name = "管理端-交易订单")
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@Slf4j
public class AdminTradeCtl {

    private final IOrderAsvc orderAsvc;

    /**
     * 运营订单分页。
     */
    @Operation(summary = "运营订单分页")
    @GetMapping
    public Result<TradePageResultDTO<TradeOrderDTO>> page(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request) {
        // 网关已校验角色，此处再认 X-Roles，防直连 order-service
        assertAdmin(request);
        log.info("admin page orders status={} keyword={}", status, keyword);
        // 运营分页：无买卖双方归属限制
        return orderAsvc.pageAdmin(status, keyword, pageNum, pageSize);
    }

    /**
     * 运营订单详情（无归属校验）。
     */
    @Operation(summary = "运营订单详情")
    @GetMapping("/{orderId}")
    public Result<TradeOrderDTO> detail(@PathVariable String orderId, HttpServletRequest request) {
        assertAdmin(request);
        log.info("admin detail orderId={}", orderId);
        // 运营详情不校验买家/卖家归属
        return orderAsvc.detailAdmin(orderId);
    }

    /**
     * 运营强制取消；body 缺 operatorId 时用 X-User-Id，且须与登录用户一致。
     */
    @Operation(summary = "运营强制取消订单")
    @PostMapping("/force-cancel")
    public Result<TradeOrderDTO> forceCancel(
            @Valid @RequestBody AdminForceCancelCommand command,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            HttpServletRequest request) {
        assertAdmin(request);
        // body 未带 operatorId 时用网关写入的登录用户
        String operatorId = StringUtils.hasText(command.getOperatorId())
                ? command.getOperatorId().trim()
                : (StringUtils.hasText(userId) ? userId.trim() : null);
        // 无登录身份则无法记审计操作人
        if (!StringUtils.hasText(operatorId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        // 禁止伪造操作人，保证审计可信
        assertOperator(userId, operatorId);
        TradeActionCommand action = new TradeActionCommand();
        action.setOrderId(command.getOrderId().trim());
        action.setOperatorId(operatorId);
        log.info("admin force-cancel orderId={} operatorId={}", action.getOrderId(), action.getOperatorId());
        // 走运营取消用例：不校验买卖双方
        return orderAsvc.cancelByAdmin(action);
    }

    private static void assertOperator(String userId, String operatorId) {
        // 网关未透传用户则视为未登录
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        // operatorId 必须与当前登录用户一致
        if (!userId.trim().equals(operatorId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "操作人与登录用户不一致");
        }
    }

    private static void assertAdmin(HttpServletRequest request) {
        String uid = request.getHeader("X-User-Id");
        // 身份头缺失：可能未走网关或 Token 未解析
        if (!StringUtils.hasText(uid)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        String roles = request.getHeader("X-Roles");
        boolean admin = StringUtils.hasText(roles)
                && Arrays.stream(roles.split(","))
                .map(s -> s.trim().toUpperCase(Locale.ROOT))
                .anyMatch(r -> "ADMIN".equals(r) || "ROLE_ADMIN".equals(r)
                        || "SUPER_ADMIN".equals(r) || "ROLE_SUPER_ADMIN".equals(r));
        // 买家/商家 Token 即使命中路由也拒绝
        if (!admin) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "需要管理员权限");
        }
    }
}
