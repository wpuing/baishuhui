package com.baishuhui.interfaces.supply.controller;

import com.baishuhui.application.service.supply.ISupplyAsvc;
import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.response.Result;
import com.baishuhui.supply.vo.SupplyInfoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 运营端供应介入：列表与强制下架。网关 + 本服务双重校验 ADMIN/SUPER_ADMIN。
 *
 * @author wei yz
 */
@Tag(name = "管理端-供应")
@RestController
@RequestMapping("/api/admin/supplies")
@RequiredArgsConstructor
@Slf4j
public class AdminSupplyCtl {

    private final ISupplyAsvc supplyAsvc;

    /**
     * 运营供应列表（含占用字段）。
     */
    @Operation(summary = "运营供应列表")
    @GetMapping
    public Result<List<SupplyInfoDTO>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        // 直连 supply-service 时仍要求管理员角色
        assertAdmin(request);
        log.info("admin list supplies category={} location={} status={}", category, location, status);
        // 运营列表保留 lockOrderId，便于判断是否须先取消订单
        return supplyAsvc.listAdmin(category, location, status);
    }

    /**
     * 运营强制下架（跳过归属）。
     */
    @Operation(summary = "运营强制下架")
    @PostMapping("/{id}/force-offline")
    public Result<SupplyInfoDTO> forceOffline(@PathVariable("id") String id, HttpServletRequest request) {
        assertAdmin(request);
        log.info("admin force-offline supplyId={}", id);
        // 跳过商家归属，仅允许 PUBLISHED 下架
        return supplyAsvc.forceUnpublish(id);
    }

    private static void assertAdmin(HttpServletRequest request) {
        String uid = request.getHeader("X-User-Id");
        // 无登录用户 id 则拒绝写操作
        if (!StringUtils.hasText(uid)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        String roles = request.getHeader("X-Roles");
        boolean admin = StringUtils.hasText(roles)
                && Arrays.stream(roles.split(","))
                .map(s -> s.trim().toUpperCase(Locale.ROOT))
                .anyMatch(r -> "ADMIN".equals(r) || "ROLE_ADMIN".equals(r)
                        || "SUPER_ADMIN".equals(r) || "ROLE_SUPER_ADMIN".equals(r));
        // 非运营角色禁止强制下架
        if (!admin) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "需要管理员权限");
        }
    }
}
