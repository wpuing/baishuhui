package com.baishuhui.interfaces.admin.controller;

import com.baishuhui.common.response.Result;
import com.baishuhui.user.vo.admin.BindRoleMenusRequest;
import com.baishuhui.user.vo.admin.MenuDTO;
import com.baishuhui.user.vo.admin.PageResultDTO;
import com.baishuhui.user.vo.admin.UpsertMenuRequest;
import com.baishuhui.application.service.menu.IMenuAsvc;
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

/**
 * 管理端动态菜单维护。
 *
 * @author wei yz
 */
@Tag(name = "管理端-菜单")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminMenuCtl {

    private final IMenuAsvc menuAsvc;

    /**
     * 菜单分页。
     */
    @Operation(summary = "菜单分页列表")
    @GetMapping("/menus")
    @PreAuthorize("hasAnyAuthority('admin:menu','admin:view')")
    public Result<PageResultDTO<MenuDTO>> page(
            @RequestParam(required = false) String clientType,
            @RequestParam(required = false) String menuType,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(menuAsvc.page(clientType, menuType, keyword, pageNum, pageSize));
    }

    /**
     * 菜单树（编辑父级用）。
     */
    @Operation(summary = "菜单树")
    @GetMapping("/menus/tree")
    @PreAuthorize("hasAnyAuthority('admin:menu','admin:view')")
    public Result<List<MenuDTO>> tree(@RequestParam(required = false) String clientType) {
        return Result.success(menuAsvc.tree(clientType));
    }

    /**
     * 新增菜单。
     */
    @Operation(summary = "新增菜单")
    @PostMapping("/menus")
    @PreAuthorize("hasAuthority('admin:menu')")
    public Result<MenuDTO> create(@Valid @RequestBody UpsertMenuRequest request) {
        return Result.success(menuAsvc.create(request));
    }

    /**
     * 更新菜单。
     */
    @Operation(summary = "更新菜单")
    @PutMapping("/menus/{id}")
    @PreAuthorize("hasAuthority('admin:menu')")
    public Result<MenuDTO> update(@PathVariable String id, @Valid @RequestBody UpsertMenuRequest request) {
        return Result.success(menuAsvc.update(id, request));
    }

    /**
     * 删除菜单。
     */
    @Operation(summary = "删除菜单")
    @DeleteMapping("/menus/{id}")
    @PreAuthorize("hasAuthority('admin:menu')")
    public Result<Void> delete(@PathVariable String id) {
        menuAsvc.delete(id);
        return Result.success();
    }

    /**
     * 查询角色已绑定菜单。
     */
    @Operation(summary = "角色已绑定菜单")
    @GetMapping("/roles/{roleCode}/menus")
    @PreAuthorize("hasAnyAuthority('admin:menu','admin:view')")
    public Result<List<String>> roleMenus(@PathVariable String roleCode) {
        return Result.success(menuAsvc.listRoleMenuIds(roleCode));
    }

    /**
     * 覆盖绑定角色菜单。
     */
    @Operation(summary = "绑定角色菜单")
    @PutMapping("/roles/{roleCode}/menus")
    @PreAuthorize("hasAuthority('admin:menu')")
    public Result<Void> bindRoleMenus(
            @PathVariable String roleCode, @Valid @RequestBody BindRoleMenusRequest request) {
        menuAsvc.bindRoleMenus(roleCode, request);
        return Result.success();
    }
}
