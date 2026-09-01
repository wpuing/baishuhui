package com.baishuhui.infrastructure.security;

import com.baishuhui.interfaces.config.InternalTokenProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 校验内部钱包接口的共享令牌，防止公网裸调扣退款。
 *
 * @author wei yz
 */
@Component
@RequiredArgsConstructor
public class InternalTokenFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Bsh-Internal-Token";

    private final InternalTokenProperties internalTokenProperties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path == null || !(path.startsWith("/internal/wallet") || path.startsWith("/internal/notifications"));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String expected = internalTokenProperties.getToken();
        if (!StringUtils.hasText(expected)) {
            reject(response, "内部令牌未配置");
            return;
        }
        String actual = request.getHeader(HEADER);
        if (!expected.equals(actual)) {
            reject(response, "内部令牌无效");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void reject(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"code\":\"UNAUTHORIZED\",\"message\":\"" + message + "\",\"data\":null}");
    }
}
