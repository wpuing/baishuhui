package com.baishuhui.interfaces.config;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 将鉴权失败映射为业务 FORBIDDEN/UNAUTHORIZED，避免落入全局 INTERNAL_ERROR。
 *
 * @author wei yz
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class SecurityAccessDeniedAdvice {

    /**
     * 已登录但权限不足（含 {@code @PreAuthorize}）。
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleAccessDenied(AccessDeniedException ex) {
        log.warn("access denied: {}", ex.getMessage());
        return Result.fail(ErrorCode.FORBIDDEN, "无权限执行该操作");
    }

    /**
     * 未认证。
     */
    @ExceptionHandler(AuthenticationException.class)
    public Result<Void> handleAuthentication(AuthenticationException ex) {
        log.warn("unauthenticated: {}", ex.getMessage());
        return Result.fail(ErrorCode.UNAUTHORIZED, "未登录或登录已失效");
    }
}
