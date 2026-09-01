package com.baishuhui.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JWT 签发与解析。Claims 字段与网关约定保持一致，扩展请新增 claim 而非改名。
 *
 * @author wei yz
 */
@Slf4j
@Service
public class JwtService {

    private static final String CLAIM_UID = "uid";
    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_ROLES = "roles";
    private static final String CLAIM_PERMS = "perms";
    private static final String CLAIM_ROLE = "role";
    private static final String DEFAULT_ROLE = "USER";
    private static final String DEMO_SECRET = "${BSH_JWT_SECRET}";

    private final SecretKey key;
    private final long expireSeconds;

    public JwtService(
            @Value("${bsh.security.jwt-secret}") String secret,
            @Value("${bsh.security.expire-seconds:86400}") long expireSeconds) {
        // 空值分支判断
        if (secret == null || secret.isBlank() || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("bsh.security.jwt-secret 须至少 32 字节，请配置 BSH_JWT_SECRET");
        }
        // 字段相等性校验
        if (DEMO_SECRET.equals(secret)) {
            log.warn("正在使用演示 JWT 密钥，生产请设置环境变量 BSH_JWT_SECRET");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expireSeconds = expireSeconds;
    }

    /**
     * @return 令牌有效期（秒）
     */
    public long getExpireSeconds() {
        return expireSeconds;
    }

    /**
     * 为已认证用户签发访问令牌。
     */
    public String issue(AuthUserPrincipal user) {
        Instant now = Instant.now();
        Map<String, Object> claims = new HashMap<>(8);
        claims.put(CLAIM_UID, user.getId());
        claims.put(CLAIM_USERNAME, user.getUsername());
        claims.put(CLAIM_ROLES, user.getRoles());
        claims.put(CLAIM_PERMS, user.getPermissions());
        // 兼容网关仅读单个 role 的旧逻辑
        claims.put(CLAIM_ROLE, user.getRoles().isEmpty() ? DEFAULT_ROLE : user.getRoles().get(0));
        return Jwts.builder()
                .subject(user.getUsername())
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expireSeconds)))
                .signWith(key)
                .compact();
    }

    /**
     * 校验签名并解析 Claims；失败由调用方捕获。
     */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 读取角色列表；无 roles 时回退到单个 role claim。
     */
    public List<String> roles(Claims claims) {
        Object v = claims.get(CLAIM_ROLES);
        // 业务条件分支
        if (v instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        Object single = claims.get(CLAIM_ROLE);
        return single == null ? List.of() : List.of(String.valueOf(single));
    }

    /**
     * 读取权限码列表。
     */
    public List<String> perms(Claims claims) {
        Object v = claims.get(CLAIM_PERMS);
        // 业务条件分支
        if (v instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return List.of();
    }
}
