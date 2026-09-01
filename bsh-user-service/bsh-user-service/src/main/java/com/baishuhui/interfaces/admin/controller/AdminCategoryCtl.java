package com.baishuhui.interfaces.admin.controller;

import com.baishuhui.application.service.category.ICategoryAsvc;
import com.baishuhui.common.response.Result;
import com.baishuhui.user.vo.category.AdminCategoryDTO;
import com.baishuhui.user.vo.category.UpsertCategoryRequest;
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
 * 管理端品类维护。
 *
 * @author wei yz
 */
@Tag(name = "管理端-品类")
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryCtl {

    private final ICategoryAsvc categoryAsvc;

    @Operation(summary = "品类列表")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('admin:category','admin:view')")
    public Result<List<AdminCategoryDTO>> list() {
        log.info("admin categories list");
        return Result.success(categoryAsvc.listAll());
    }

    @Operation(summary = "新增品类")
    @PostMapping
    @PreAuthorize("hasAuthority('admin:category')")
    public Result<AdminCategoryDTO> create(@Valid @RequestBody UpsertCategoryRequest request) {
        log.info("admin category create name={}", request.getName());
        return Result.success(categoryAsvc.create(request));
    }

    @Operation(summary = "更新品类")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:category')")
    public Result<AdminCategoryDTO> update(
            @PathVariable String id,
            @Valid @RequestBody UpsertCategoryRequest request) {
        log.info("admin category update id={}", id);
        return Result.success(categoryAsvc.update(id, request));
    }

    @Operation(summary = "删除品类")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:category')")
    public Result<Void> delete(@PathVariable String id) {
        log.info("admin category delete id={}", id);
        categoryAsvc.delete(id);
        return Result.success();
    }
}
