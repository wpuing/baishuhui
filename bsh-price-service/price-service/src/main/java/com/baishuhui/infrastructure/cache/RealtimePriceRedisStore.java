package com.baishuhui.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/**
 * 实时行情 Redis 访问。Key：bsh:price:realtime:{sku}。
 *
 * @author wei yz
 */
@Component
@Profile("!demo & !standalone")
@RequiredArgsConstructor
public class RealtimePriceRedisStore {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * put。
     */
    public void put(String sku, String priceJson, Duration ttl) {
        stringRedisTemplate.opsForValue().set(RedisKeyConstants.realtimePrice(sku), priceJson, ttl);
    }

    /**
     * 读取。
     */
    public Optional<String> get(String sku) {
        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(RedisKeyConstants.realtimePrice(sku)));
    }
}
