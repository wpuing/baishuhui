package com.baishuhui.infrastructure.security;

import com.baishuhui.common.constant.ErrorCode;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 从 Authorization Bearer 解析 JWT 并写入 SecurityContext。
 * 解析失败时清空上下文并放行，由下游接口决定是否要求登录。
 *
 * @author wei yz
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ROLE_PREFIX = "ROLE_";
    private static final String STATUS_ACTIVE = "ACTIVE";

    private final JwtService jwtService;
    private final TokenCutoffService tokenCutoffService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        // 已有认证或非 Bearer 头时跳过，避免覆盖上游设置
        if (auth != null && auth.startsWith(BEARER_PREFIX)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Claims claims = jwtService.parse(auth.substring(BEARER_PREFIX.length()).trim());
                Object uidObj = claims.get("uid");
                String uid = uidObj == null ? null : String.valueOf(uidObj);
                // 条件不满足时走异常或跳过
                if (!isAuthAnonymous(request.getRequestURI()) && tokenCutoffService.revoked(uid, claims.getIssuedAt())) {
                    SecurityContextHolder.clearContext();
                    writeUnauthorized(response);
                    return;
                }
                List<String> roles = jwtService.roles(claims);
                List<String> perms = jwtService.perms(claims);
                String username = claims.get("username", String.class);
                // 兼容仅有 subject、无 username claim 的旧 token
                if (username == null) {
                    username = claims.getSubject();
                }
                AuthUserPrincipal principal = new AuthUserPrincipal(
                        uid,
                        username,
                        "",
                        username,
                        STATUS_ACTIVE,
                        roles,
                        perms);
                var authorities = Stream.concat(
                                roles.stream().map(r -> ROLE_PREFIX + r),
                                perms.stream())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toCollection(ArrayList::new));
                var token = new UsernamePasswordAuthenticationToken(principal, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(token);
            } catch (Exception ignored) {
                // 令牌非法时不阻断匿名访问公开接口
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isAuthAnonymous(String uri) {
        // 空值分支判断
        if (uri == null) {
            return false;
        }
        return uri.contains("/api/auth/login")
                || uri.contains("/api/auth/captcha")
                || uri.contains("/api/auth/register");
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"code\":\"" + ErrorCode.UNAUTHORIZED
                + "\",\"message\":\"登录已失效，请重新登录\",\"data\":null}");
    }
}
