package com.baishuhui.interfaces.menu.controller;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.response.Result;
import com.baishuhui.user.vo.admin.MenuDTO;
import com.baishuhui.application.service.menu.IMenuAsvc;
import com.baishuhui.infrastructure.security.AuthUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 端侧动态菜单（登录用户按角色拉取）。
 *
 * @author wei yz
 */
@Tag(name = "端侧-菜单")
@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
@Slf4j
public class MenuMineCtl {

    private final IMenuAsvc menuAsvc;

    /**
     * 当前用户菜单树。
     */
    @Operation(summary = "我的菜单")
    @GetMapping("/mine")
    public Result<List<MenuDTO>> mine(@RequestParam String clientType) {
        log.info("mine invoked");
        return Result.success(menuAsvc.mine(currentUserId(), clientType));
    }

    private String currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthUserPrincipal principal)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录");
        }
        return principal.getId();
    }
}
