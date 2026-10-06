package com.baishuhui.infrastructure.security;

import com.baishuhui.domain.admin.entity.IpRuleEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 将 MySQL IP 规则同步到 Redis，供多实例网关热读（网关仍以快照接口为准）。
 *
 * @author wei yz
 */
@Slf4j
@Component
public class IpAccessRedisSupport {

    private final StringRedisTemplate redisTemplate;

    public IpAccessRedisSupport(@Autowired(required = false) StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 全量覆盖 Redis 白/黑名单。
     *
     * @param rules 未删除的有效规则
     */
    public void reload(List<IpRuleEntity> rules) {
        if (redisTemplate == null) {
            return;
        }
        try {
            redisTemplate.delete(IpAccessConstants.REDIS_WHITELIST);
            redisTemplate.delete(IpAccessConstants.REDIS_BLACKLIST);
            if (rules == null || rules.isEmpty()) {
                return;
            }
            Set<String> white = new HashSet<>();
            LocalDateTime now = LocalDateTime.now();
            for (IpRuleEntity rule : rules) {
                if (rule.getIp() == null || rule.getIp().isBlank()) {
                    continue;
                }
                if (IpAccessConstants.TYPE_WHITELIST.equals(rule.getRuleType())) {
                    white.add(rule.getIp().trim());
                    continue;
                }
                if (!IpAccessConstants.TYPE_BLACKLIST.equals(rule.getRuleType())) {
                    continue;
                }
                if (rule.getExpireTime() != null && !rule.getExpireTime().isAfter(now)) {
                    continue;
                }
                long epoch = 0L;
                if (rule.getExpireTime() != null) {
                    epoch = rule.getExpireTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                }
                redisTemplate.opsForHash().put(IpAccessConstants.REDIS_BLACKLIST, rule.getIp().trim(),
                        String.valueOf(epoch));
            }
            if (!white.isEmpty()) {
                redisTemplate.opsForSet().add(IpAccessConstants.REDIS_WHITELIST, white.toArray(String[]::new));
            }
        } catch (Exception ex) {
            log.warn("sync ip rules to redis failed: {}", ex.getMessage());
        }
    }
}
