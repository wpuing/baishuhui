package com.baishuhui.interfaces.admin.controller;

import com.baishuhui.common.response.Result;
import com.baishuhui.user.vo.admin.PageResultDTO;
import com.baishuhui.user.vo.admin.RedisKvRequest;
import com.baishuhui.user.vo.admin.SysConfigDTO;
import com.baishuhui.user.vo.admin.UpsertSysConfigRequest;
import com.baishuhui.application.service.admin.ISysConfigAsvc;
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

import java.util.List;
import java.util.Map;

/**
 * 管理端系统参数与受控 Redis。
 *
 * @author wei yz
 */
@Tag(name = "管理端-系统配置")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminSysConfigCtl {

    private final ISysConfigAsvc sysConfigAsvc;

    @Operation(summary = "参数配置分页")
    @GetMapping("/sys-configs")
    @PreAuthorize("hasAnyAuthority('admin:config','admin:view')")
    public Result<PageResultDTO<SysConfigDTO>> pageConfigs(
            @RequestParam(required = false) String groupCode,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(sysConfigAsvc.page(groupCode, pageNum, pageSize));
    }

    @Operation(summary = "新增参数")
    @PostMapping("/sys-configs")
    @PreAuthorize("hasAuthority('admin:config')")
    public Result<SysConfigDTO> createConfig(@Valid @RequestBody UpsertSysConfigRequest request) {
        return Result.success(sysConfigAsvc.create(request));
    }

    @Operation(summary = "更新参数")
    @PutMapping("/sys-configs/{id}")
    @PreAuthorize("hasAuthority('admin:config')")
    public Result<SysConfigDTO> updateConfig(
            @PathVariable String id, @Valid @RequestBody UpsertSysConfigRequest request) {
        return Result.success(sysConfigAsvc.update(id, request));
    }

    @Operation(summary = "删除参数")
    @DeleteMapping("/sys-configs/{id}")
    @PreAuthorize("hasAuthority('admin:config')")
    public Result<Void> deleteConfig(@PathVariable String id) {
        sysConfigAsvc.delete(id);
        return Result.success();
    }

    @Operation(summary = "Redis 键列表（受控前缀）")
    @GetMapping("/redis-keys")
    @PreAuthorize("hasAuthority('admin:config')")
    public Result<List<String>> listRedisKeys(@RequestParam(required = false) String prefix) {
        return Result.success(sysConfigAsvc.listRedisKeys(prefix));
    }

    @Operation(summary = "读取 Redis 值")
    @GetMapping("/redis-keys/value")
    @PreAuthorize("hasAuthority('admin:config')")
    public Result<Map<String, String>> getRedisValue(@RequestParam String key) {
        return Result.success(sysConfigAsvc.getRedisValue(key));
    }

    @Operation(summary = "写入 Redis")
    @PostMapping("/redis-keys")
    @PreAuthorize("hasAuthority('admin:config')")
    public Result<Void> setRedisValue(@Valid @RequestBody RedisKvRequest request) {
        sysConfigAsvc.setRedisValue(request);
        return Result.success();
    }

    @Operation(summary = "删除 Redis 键")
    @DeleteMapping("/redis-keys")
    @PreAuthorize("hasAuthority('admin:config')")
    public Result<Void> deleteRedisKey(@RequestParam String key) {
        sysConfigAsvc.deleteRedisKey(key);
        return Result.success();
    }
}
