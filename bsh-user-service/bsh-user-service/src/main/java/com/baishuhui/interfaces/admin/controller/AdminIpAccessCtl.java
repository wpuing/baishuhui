package com.baishuhui.interfaces.admin.controller;

import com.baishuhui.common.response.Result;
import com.baishuhui.user.vo.admin.IpRuleDTO;
import com.baishuhui.user.vo.admin.PageResultDTO;
import com.baishuhui.user.vo.admin.UpsertIpRuleRequest;
import com.baishuhui.application.service.admin.IIpAccessAsvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 IP 白名单 / 黑名单。
 *
 * @author wei yz
 */
@Tag(name = "管理端-IP访问控制")
@RestController
@RequestMapping("/api/admin/ip-rules")
@RequiredArgsConstructor
@Slf4j
public class AdminIpAccessCtl {

    private final IIpAccessAsvc ipAccessAsvc;

    /**
     * 分页查询 IP 规则。
     */
    @Operation(summary = "IP 规则分页列表")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('admin:ip','admin:view')")
    public Result<PageResultDTO<IpRuleDTO>> list(
            @RequestParam(required = false) String ruleType,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        log.info("list invoked");
        return Result.success(ipAccessAsvc.page(ruleType, pageNum, pageSize));
    }

    /**
     * 加入白名单，网关对该 IP 跳过限流与自动拉黑。
     */
    @Operation(summary = "加入白名单")
    @PostMapping("/whitelist")
    @PreAuthorize("hasAnyAuthority('admin:ip','admin:view')")
    public Result<IpRuleDTO> addWhitelist(@Valid @RequestBody UpsertIpRuleRequest request) {
        log.info("addWhitelist invoked");
        return Result.success(ipAccessAsvc.addWhitelist(request));
    }

    /**
     * 手工拉黑，网关立即拒绝该 IP。
     */
    @Operation(summary = "加入黑名单")
    @PostMapping("/blacklist")
    @PreAuthorize("hasAnyAuthority('admin:ip','admin:view')")
    public Result<IpRuleDTO> addBlacklist(@Valid @RequestBody UpsertIpRuleRequest request) {
        log.info("addBlacklist invoked");
        return Result.success(ipAccessAsvc.addBlacklist(request));
    }

    /**
     * 移除白名单或黑名单。
     */
    @Operation(summary = "移除规则")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('admin:ip','admin:view')")
    public Result<Void> remove(@PathVariable String id) {
        log.info("remove invoked");
        ipAccessAsvc.remove(id);
        return Result.success();
    }
}
