package com.baishuhui.interfaces.auth.controller;

import com.baishuhui.common.response.Result;
import com.baishuhui.user.vo.auth.CaptchaDTO;
import com.baishuhui.user.vo.auth.LoginRequest;
import com.baishuhui.user.vo.auth.LoginResultDTO;
import com.baishuhui.user.vo.auth.RegisterRequest;
import com.baishuhui.user.vo.auth.UserViewDTO;
import com.baishuhui.application.service.auth.IUserAsvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baishuhui.user.vo.auth.ChangePasswordRequest;
import com.baishuhui.user.vo.auth.UpdateProfileRequest;

import java.util.Map;

/**
 * 认证相关接口：验证码、登录、注册、当前用户。
 *
 * @author wei yz
 */
@Tag(name = "认证")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthCtl {

    private final IUserAsvc userAsvc;

    /**
     * 获取图形验证码（一次性消费）。
     */
    @Operation(summary = "图形验证码")
    @GetMapping("/captcha")
    public Result<CaptchaDTO> captcha() {
        return Result.success(userAsvc.createCaptcha());
    }

    /**
     * 账号密码登录：先校验验证码，再签发 JWT；计入今日登录访问。
     */
    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginResultDTO> login(
            @Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        return Result.success(userAsvc.login(request, resolveClientIp(httpRequest)));
    }

    /**
     * 业务用户注册（待审核）。
     */
    @Operation(summary = "注册")
    @PostMapping("/register")
    public Result<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        String id = userAsvc.register(request);
        return Result.success(Map.of("userId", id));
    }

    /**
     * 当前登录用户信息；未认证时返回 UNAUTHORIZED。
     */
    @Operation(summary = "当前用户")
    @GetMapping("/me")
    public Result<UserViewDTO> me() {
        return Result.success(userAsvc.currentUser());
    }

    /**
     * 更新个人信息。
     */
    @Operation(summary = "更新个人信息")
    @PutMapping("/profile")
    public Result<UserViewDTO> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return Result.success(userAsvc.updateProfile(request));
    }

    /**
     * 修改密码。
     */
    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userAsvc.changePassword(request);
        return Result.success();
    }

    private static String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(realIp)) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }
}
