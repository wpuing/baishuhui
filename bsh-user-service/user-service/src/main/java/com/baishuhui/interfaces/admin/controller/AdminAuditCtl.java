package com.baishuhui.interfaces.admin.controller;

import com.baishuhui.user.vo.wallet.PaymentResultDTO;
import com.baishuhui.common.response.Result;
import com.baishuhui.user.vo.admin.AuditRejectRequest;
import com.baishuhui.user.vo.admin.AuditUpdateRequest;
import com.baishuhui.user.vo.admin.AuditUserDTO;
import com.baishuhui.user.vo.admin.PageResultDTO;
import com.baishuhui.user.vo.admin.WalletAdjustRequest;
import com.baishuhui.application.service.admin.IUserAuditAsvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端用户审核。
 *
 * @author wei yz
 */
@Tag(name = "管理端-用户审核")
@RestController
@RequestMapping("/api/admin/audits")
@RequiredArgsConstructor
public class AdminAuditCtl {

    private final IUserAuditAsvc userAuditAsvc;

    /**
     * 审核分页列表。
     */
    @Operation(summary = "用户审核分页")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('admin:audit','admin:user','admin:view')")
    public Result<PageResultDTO<AuditUserDTO>> page(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(userAuditAsvc.pageAudits(status, role, pageNum, pageSize));
    }

    /**
     * 用户详情。
     */
    @Operation(summary = "用户审核详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('admin:audit','admin:user','admin:view')")
    public Result<AuditUserDTO> detail(@PathVariable String id) {
        return Result.success(userAuditAsvc.detail(id));
    }

    /**
     * 修改用户资料（仅超管）。
     */
    @Operation(summary = "修改审核用户")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:user:write')")
    public Result<AuditUserDTO> update(@PathVariable String id, @Valid @RequestBody AuditUpdateRequest request) {
        return Result.success(userAuditAsvc.update(id, request));
    }

    /**
     * 删除用户（仅超管）。
     */
    @Operation(summary = "删除审核用户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:user:delete')")
    public Result<Void> delete(@PathVariable String id) {
        userAuditAsvc.delete(id);
        return Result.success();
    }

    /**
     * 开始审核。
     */
    @Operation(summary = "开始审核")
    @PostMapping("/{id}/start")
    @PreAuthorize("hasAnyAuthority('admin:audit','admin:user')")
    public Result<AuditUserDTO> start(@PathVariable String id) {
        return Result.success(userAuditAsvc.start(id));
    }

    /**
     * 审核通过。
     */
    @Operation(summary = "审核通过")
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyAuthority('admin:audit','admin:user')")
    public Result<AuditUserDTO> approve(@PathVariable String id) {
        return Result.success(userAuditAsvc.approve(id));
    }

    /**
     * 审核不通过。
     */
    @Operation(summary = "审核不通过")
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyAuthority('admin:audit','admin:user')")
    public Result<AuditUserDTO> reject(
            @PathVariable String id, @Valid @RequestBody(required = false) AuditRejectRequest request) {
        return Result.success(userAuditAsvc.reject(id, request == null ? new AuditRejectRequest() : request));
    }

    /**
     * 测试期向用户渠道调账（结单尾款不足时补余额）。
     */
    @Operation(summary = "用户钱包调账")
    @PostMapping("/{id}/adjust")
    @PreAuthorize("hasAnyAuthority('admin:user:write','admin:audit')")
    public Result<PaymentResultDTO> adjust(@PathVariable String id, @Valid @RequestBody WalletAdjustRequest request) {
        return Result.success(userAuditAsvc.adjustWallet(id, request));
    }
}
