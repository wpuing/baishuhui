package com.baishuhui.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 网关侧 JWT 解析与角色/权限声明提取。
 *
 * @author wei yz
 */
@Slf4j
@Component
public class JwtTokenService {

    private static final String DEMO_SECRET = "${BSH_JWT_SECRET}";

    private final SecretKey key;

    public JwtTokenService(
            @Value("${bsh.security.jwt-secret}") String secret) {
        if (secret == null || secret.isBlank() || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("bsh.security.jwt-secret 须至少 32 字节，请配置 BSH_JWT_SECRET");
        }
        if (DEMO_SECRET.equals(secret)) {
            log.warn("正在使用演示 JWT 密钥，生产请设置环境变量 BSH_JWT_SECRET");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 验签并解析 Token 载荷。
     */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 Claims 提取角色列表（兼容 roles / role）。
     */
    @SuppressWarnings("unchecked")
    public List<String> roles(Claims claims) {
        Object v = claims.get("roles");
        if (v instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        Object single = claims.get("role");
        return single == null ? List.of() : List.of(String.valueOf(single));
    }

    /**
     * 从 Claims 提取权限码列表。
     */
    @SuppressWarnings("unchecked")
    public List<String> perms(Claims claims) {
        Object v = claims.get("perms");
        if (v instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return List.of();
    }
}
