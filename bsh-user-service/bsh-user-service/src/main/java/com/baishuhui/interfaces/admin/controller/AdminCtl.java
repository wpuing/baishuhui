package com.baishuhui.interfaces.admin.controller;

import com.baishuhui.common.response.Result;
import com.baishuhui.user.vo.admin.AdminUserBriefDTO;
import com.baishuhui.application.service.auth.IUserAsvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端用户相关 REST 接口。
 *
 * @author wei yz
 */
@Tag(name = "管理端")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminCtl {

    private final IUserAsvc userAsvc;

    /**
     * 管理端健康检查。
     */
    @Operation(summary = "健康检查")
    @GetMapping("/health")
    public Result<String> health() {
        log.info("health invoked");
        return Result.success("admin-ok");
    }

    /**
     * 用户简要列表（需 admin:user 权限）。
     */
    @Operation(summary = "用户列表")
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('admin:user')")
    public Result<List<AdminUserBriefDTO>> users() {
        log.info("users invoked");
        return Result.success(userAsvc.listAdminUsers());
    }
}
