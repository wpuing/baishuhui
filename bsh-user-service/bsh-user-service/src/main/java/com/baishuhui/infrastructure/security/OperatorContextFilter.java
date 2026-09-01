package com.baishuhui.infrastructure.security;

import com.baishuhui.common.persistence.OperatorContext;
import com.baishuhui.common.persistence.OperatorInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 将当前登录用户写入 OperatorContext，供 MetaObjectHandler 填充审计字段。
 * 仅通过 SecurityFilterChain 注册，避免 Servlet 容器重复注册。
 *
 * @author wei yz
 */
public class OperatorContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            OperatorContext.set(resolveOperator());
            filterChain.doFilter(request, response);
        } finally {
            OperatorContext.clear();
        }
    }

    private OperatorInfo resolveOperator() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // 空值分支判断
        if (auth != null && auth.getPrincipal() instanceof AuthUserPrincipal principal) {
            String name = principal.getNickname();
            // 空值分支判断
            if (name == null || name.isBlank()) {
                name = principal.getUsername();
            }
            return new OperatorInfo(principal.getId(), name);
        }
        return OperatorInfo.system();
    }
}
