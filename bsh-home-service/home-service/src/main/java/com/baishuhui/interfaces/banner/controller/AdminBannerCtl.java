package com.baishuhui.interfaces.banner.controller;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.response.Result;
import com.baishuhui.home.vo.UpsertBannerRequest;
import com.baishuhui.application.service.banner.IBannerAdminAsvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 管理端 Banner；网关已校验 JWT，此处校验 ADMIN / banner:manage。
 *
 * @author wei yz
 */
@Tag(name = "管理端-内容配置")
@RestController
@RequestMapping("/api/admin/banners")
@RequiredArgsConstructor
public class AdminBannerCtl {

    private final IBannerAdminAsvc bannerAdminAsvc;

    @Operation(summary = "Banner 列表")
    @GetMapping
    public Result<List<Map<String, Object>>> list(HttpServletRequest request) {
        assertAdmin(request);
        return Result.success(bannerAdminAsvc.listAll());
    }

    @Operation(summary = "新增 Banner")
    @PostMapping
    public Result<Map<String, Object>> create(
            HttpServletRequest request, @Valid @RequestBody UpsertBannerRequest body) {
        assertAdmin(request);
        return Result.success(bannerAdminAsvc.create(body));
    }

    @Operation(summary = "更新 Banner")
    @PutMapping("/{id}")
    public Result<Map<String, Object>> update(
            HttpServletRequest request,
            @PathVariable String id,
            @Valid @RequestBody UpsertBannerRequest body) {
        assertAdmin(request);
        return Result.success(bannerAdminAsvc.update(id, body));
    }

    @Operation(summary = "删除 Banner")
    @DeleteMapping("/{id}")
    public Result<Void> delete(HttpServletRequest request, @PathVariable String id) {
        assertAdmin(request);
        bannerAdminAsvc.delete(id);
        return Result.success();
    }

    private static void assertAdmin(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        String roles = request.getHeader("X-Roles");
        String perms = request.getHeader("X-Perms");
        boolean admin = StringUtils.hasText(roles)
                && Arrays.stream(roles.split(","))
                .map(s -> s.trim().toUpperCase(Locale.ROOT))
                .anyMatch(r -> "ADMIN".equals(r) || "ROLE_ADMIN".equals(r)
                        || "SUPER_ADMIN".equals(r) || "ROLE_SUPER_ADMIN".equals(r));
        boolean manage = StringUtils.hasText(perms)
                && Arrays.stream(perms.split(","))
                .map(String::trim)
                .anyMatch("banner:manage"::equals);
        if (!admin && !manage) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无内容配置权限");
        }
    }
}
