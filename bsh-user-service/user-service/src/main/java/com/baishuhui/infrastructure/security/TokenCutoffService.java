package com.baishuhui.infrastructure.security;

import com.baishuhui.infrastructure.cache.RedisKeyConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * 改密 / 删用户后写入 JWT 签发水位；iat 不大于水位的令牌视为已作废。
 *
 * @author wei yz
 */
@Slf4j
@Service
public class TokenCutoffService {

    private final ObjectProvider<StringRedisTemplate> redisProvider;
    private final long expireSeconds;

    public TokenCutoffService(
            ObjectProvider<StringRedisTemplate> redisProvider,
            @Value("${bsh.security.expire-seconds:86400}") long expireSeconds) {
        this.redisProvider = redisProvider;
        this.expireSeconds = expireSeconds <= 0 ? 86400L : expireSeconds;
    }

    /**
     * 将当前时间写入 cutoff，使此前签发的 Token 立即失效。
     *
     * @param userId 用户 id
     */
    public void revoke(String userId) {
        if (!StringUtils.hasText(userId)) {
            return;
        }
        StringRedisTemplate redis = redisProvider.getIfAvailable();
        if (redis == null) {
            log.warn("token cutoff skip, redis unavailable userId={}", userId);
            return;
        }
        String key = RedisKeyConstants.tokenCutoff(userId.trim());
        long now = Instant.now().getEpochSecond();
        try {
            redis.opsForValue().set(key, String.valueOf(now), Duration.ofSeconds(expireSeconds + 3600L));
            log.info("token cutoff set userId={} cutoff={}", userId, now);
        } catch (Exception ex) {
            log.error("token cutoff write fail userId={}", userId, ex);
        }
    }

    /**
     * 判断令牌是否已作废。Redis 故障时放行并打日志。
     *
     * @param userId    用户 id
     * @param issuedAt  JWT iat
     * @return true 表示应拒绝
     */
    public boolean revoked(String userId, Date issuedAt) {
        if (!StringUtils.hasText(userId)) {
            return false;
        }
        StringRedisTemplate redis = redisProvider.getIfAvailable();
        if (redis == null) {
            return false;
        }
        try {
            String raw = redis.opsForValue().get(RedisKeyConstants.tokenCutoff(userId.trim()));
            if (!StringUtils.hasText(raw)) {
                return false;
            }
            long cutoff = Long.parseLong(raw.trim());
            long iat = issuedAt == null ? 0L : issuedAt.toInstant().getEpochSecond();
            return iat < cutoff;
        } catch (Exception ex) {
            log.warn("token cutoff read fail userId={}", userId, ex);
            return false;
        }
    }
}
