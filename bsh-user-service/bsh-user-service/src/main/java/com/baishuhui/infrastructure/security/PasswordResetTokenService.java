package com.baishuhui.infrastructure.security;

import com.baishuhui.infrastructure.cache.RedisKeyConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 找回密码令牌：优先 Redis，不可用时本地缓存；TTL 10 分钟。
 *
 * @author wei yz
 */
@Slf4j
@Service
public class PasswordResetTokenService {

    public static final long EXPIRE_SECONDS = 600L;

    private static final Duration TTL = Duration.ofSeconds(EXPIRE_SECONDS);
    private static final int LOCAL_MAX = 2000;

    private final StringRedisTemplate redisTemplate;
    private final ConcurrentHashMap<String, LocalEntry> localStore = new ConcurrentHashMap<>();

    public PasswordResetTokenService(@Autowired(required = false) StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private record LocalEntry(String userId, long expireAtMs) {
        boolean expired(long now) {
            return now > expireAtMs;
        }
    }

    /**
     * 生成并存储重置令牌，返回 token（不含 key 前缀）。
     *
     * @param userId 用户 id
     * @return 重置令牌
     */
    public String issue(String userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        store(token, userId);
        return token;
    }

    /**
     * 消费令牌并返回 userId；无效或过期返回 null。
     *
     * @param token 重置令牌
     * @return 用户 id 或 null
     */
    public String consume(String token) {
        // 空入参直接失败
        if (!StringUtils.hasText(token)) {
            return null;
        }
        String key = token.trim();
        // 优先 Redis 取删
        if (redisTemplate != null) {
            try {
                String redisKey = RedisKeyConstants.pwdReset(key);
                String userId = redisTemplate.opsForValue().get(redisKey);
                // 取到则删除保证一次性
                if (userId != null) {
                    redisTemplate.delete(redisKey);
                    return userId;
                }
            } catch (Exception ex) {
                log.warn("pwd-reset redis consume fail, fallback local", ex);
            }
        }
        LocalEntry entry = localStore.remove(key);
        // 空或过期
        if (entry == null || entry.expired(System.currentTimeMillis())) {
            return null;
        }
        return entry.userId();
    }

    private void store(String token, String userId) {
        // Redis 可用写远程
        if (redisTemplate != null) {
            try {
                redisTemplate.opsForValue().set(RedisKeyConstants.pwdReset(token), userId, TTL);
                return;
            } catch (Exception ex) {
                log.warn("pwd-reset redis store fail, fallback local", ex);
            }
        }
        storeLocal(token, userId);
    }

    private void storeLocal(String token, String userId) {
        long now = System.currentTimeMillis();
        localStore.entrySet().removeIf(e -> e.getValue().expired(now));
        // 容量满时剔除约 10%
        if (localStore.size() >= LOCAL_MAX) {
            int remove = Math.max(LOCAL_MAX / 10, 1);
            Iterator<String> it = localStore.keySet().iterator();
            while (it.hasNext() && remove > 0) {
                it.next();
                it.remove();
                remove--;
            }
        }
        localStore.put(token, new LocalEntry(userId, now + TTL.toMillis()));
    }
}
