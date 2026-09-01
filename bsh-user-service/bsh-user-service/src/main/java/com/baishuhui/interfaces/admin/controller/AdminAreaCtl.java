package com.baishuhui.interfaces.admin.controller;

import com.baishuhui.common.response.Result;
import com.baishuhui.user.vo.admin.AreaDTO;
import com.baishuhui.user.vo.admin.UpsertAreaRequest;
import com.baishuhui.application.service.area.IAreaAsvc;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端地区维护。
 *
 * @author wei yz
 */
@Tag(name = "管理端-地区")
@RestController
@RequestMapping("/api/admin/areas")
@RequiredArgsConstructor
@Slf4j
public class AdminAreaCtl {

    private final IAreaAsvc areaAsvc;

    @Operation(summary = "地区树")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('admin:area','admin:view')")
    public Result<List<AreaDTO>> tree() {
        log.info("tree invoked");
        return Result.success(areaAsvc.tree());
    }

    @Operation(summary = "新增地区")
    @PostMapping
    @PreAuthorize("hasAuthority('admin:area')")
    public Result<AreaDTO> create(@Valid @RequestBody UpsertAreaRequest request) {
        log.info("create invoked");
        return Result.success(areaAsvc.create(request));
    }

    @Operation(summary = "更新地区")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:area')")
    public Result<AreaDTO> update(@PathVariable String id, @Valid @RequestBody UpsertAreaRequest request) {
        log.info("update invoked");
        return Result.success(areaAsvc.update(id, request));
    }

    @Operation(summary = "删除地区")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:area')")
    public Result<Void> delete(@PathVariable String id) {
        log.info("delete invoked");
        areaAsvc.delete(id);
        return Result.success();
    }
}
