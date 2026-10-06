package com.baishuhui.application.service.admin;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.domain.admin.entity.SysConfigEntity;
import com.baishuhui.domain.admin.repositories.ISysConfigRepository;
import com.baishuhui.domain.support.PageData;
import com.baishuhui.infrastructure.cache.RedisKeyConstants;
import com.baishuhui.user.vo.admin.PageResultDTO;
import com.baishuhui.user.vo.admin.RedisKvRequest;
import com.baishuhui.user.vo.admin.SysConfigDTO;
import com.baishuhui.user.vo.admin.UpsertSysConfigRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 系统参数与受控 Redis 键管理。
 *
 * @author wei yz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysConfigAsvcImpl implements ISysConfigAsvc {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final int MAX_PAGE_SIZE = 50;

    private static final Set<String> ALLOWED_REDIS_PREFIX = Set.of(
            RedisKeyConstants.CONFIG_PREFIX,
            RedisKeyConstants.DICT_PREFIX,
            RedisKeyConstants.STATS_PREFIX);

    private final ISysConfigRepository sysConfigRepository;

    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 启动时把需同步的参数刷进 Redis。
     */
    @PostConstruct
    @Override
    public void warmRedis() {
        List<SysConfigEntity> list = sysConfigRepository.listSyncRedis();
        // 空值分支判断
        if (list == null || list.isEmpty() || stringRedisTemplate == null) {
            return;
        }
        // 遍历集合逐项处理
        for (SysConfigEntity row : list) {
            stringRedisTemplate.opsForValue().set(RedisKeyConstants.config(row.getConfigKey()), row.getConfigValue());
        }
        log.info("sys config synced to redis count={}", list.size());
    }

    /**
     * 参数分页。
     */
    @Override
    public PageResultDTO<SysConfigDTO> page(String groupCode, int pageNum, int pageSize) {
        int num = Math.max(pageNum, 1);
        int size = pageSize <= 0 ? DEFAULT_PAGE_SIZE : Math.min(pageSize, MAX_PAGE_SIZE);
        PageData<SysConfigEntity> mp = sysConfigRepository.page(groupCode, num, size);
        // 空值分支判断
        if (mp.records() == null || mp.records().isEmpty()) {
            return PageResultDTO.of(List.of(), 0, num, size);
        }
        List<SysConfigDTO> list = new ArrayList<>(mp.records().size());
        // 遍历集合逐项处理
        for (SysConfigEntity e : mp.records()) {
            list.add(toDto(e));
        }
        return PageResultDTO.of(list, mp.total(), num, size);
    }

    /**
     * 新增参数。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public SysConfigDTO create(UpsertSysConfigRequest request) {
        // 业务条件分支
        if (sysConfigRepository.existsKey(request.getConfigKey(), null)) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "配置键已存在");
        }
        SysConfigEntity entity = new SysConfigEntity();
        apply(entity, request);
        sysConfigRepository.insert(entity);
        syncRedisIfNeed(entity);
        log.info("sys config created key={}", entity.getConfigKey());
        return toDto(entity);
    }

    /**
     * 更新参数。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public SysConfigDTO update(String id, UpsertSysConfigRequest request) {
        SysConfigEntity entity = sysConfigRepository.getById(id);
        // 空值分支判断
        if (entity == null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "配置不存在");
        }
        // 业务条件分支
        if (sysConfigRepository.existsKey(request.getConfigKey(), id)) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "配置键已存在");
        }
        apply(entity, request);
        sysConfigRepository.updateById(entity);
        syncRedisIfNeed(entity);
        log.info("sys config updated id={}", id);
        return toDto(entity);
    }

    /**
     * 删除参数。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String id) {
        SysConfigEntity entity = sysConfigRepository.getById(id);
        // 空值分支判断
        if (entity == null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "配置不存在");
        }
        sysConfigRepository.deleteById(id);
        // 空值分支判断
        if (stringRedisTemplate != null && Integer.valueOf(1).equals(entity.getSyncRedis())) {
            stringRedisTemplate.delete(RedisKeyConstants.config(entity.getConfigKey()));
        }
        log.info("sys config deleted id={}", id);
    }

    /**
     * 列出受控前缀下的 Redis 键。
     */
    @Override
    public List<String> listRedisKeys(String prefix) {
        requireRedis();
        String p = normalizeAllowedPrefix(prefix);
        Set<String> keys = stringRedisTemplate.keys(p + "*");
        // 空值分支判断
        if (keys == null || keys.isEmpty()) {
            return List.of();
        }
        return keys.stream().sorted().limit(200).collect(Collectors.toList());
    }

    /**
     * 读取 Redis 值（按类型：string/list/hash/set）。
     */
    @Override
    public Map<String, String> getRedisValue(String key) {
        requireRedis();
        assertAllowedKey(key);
        try {
            org.springframework.data.redis.connection.DataType type = stringRedisTemplate.type(key);
            // 空值分支判断
            if (type == null || type == org.springframework.data.redis.connection.DataType.NONE) {
                return Map.of("key", key, "type", "none", "value", "");
            }
            String typeName = type.code();
            // 业务条件分支
            if (type == org.springframework.data.redis.connection.DataType.STRING) {
                String v = stringRedisTemplate.opsForValue().get(key);
                return Map.of("key", key, "type", typeName, "value", v == null ? "" : v);
            }
            // 业务条件分支
            if (type == org.springframework.data.redis.connection.DataType.LIST) {
                List<String> list = stringRedisTemplate.opsForList().range(key, 0, 49);
                return Map.of("key", key, "type", typeName, "value", list == null ? "[]" : list.toString());
            }
            // 业务条件分支
            if (type == org.springframework.data.redis.connection.DataType.HASH) {
                Map<Object, Object> hash = stringRedisTemplate.opsForHash().entries(key);
                return Map.of("key", key, "type", typeName, "value", hash == null ? "{}" : hash.toString());
            }
            // 业务条件分支
            if (type == org.springframework.data.redis.connection.DataType.SET) {
                Set<String> set = stringRedisTemplate.opsForSet().members(key);
                return Map.of("key", key, "type", typeName, "value", set == null ? "[]" : set.toString());
            }
            // 业务条件分支
            if (type == org.springframework.data.redis.connection.DataType.ZSET) {
                Set<String> zset = stringRedisTemplate.opsForZSet().range(key, 0, 49);
                return Map.of("key", key, "type", typeName, "value", zset == null ? "[]" : zset.toString());
            }
            return Map.of("key", key, "type", typeName, "value", "(unsupported type)");
        } catch (Exception ex) {
            log.warn("redis get fail key={}: {}", key, ex.getMessage());
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "读取 Redis 失败: " + ex.getMessage());
        }
    }

    /**
     * 写入 Redis。
     */
    @Override
    public void setRedisValue(RedisKvRequest request) {
        requireRedis();
        assertAllowedKey(request.getKey());
        // 空值分支判断
        if (request.getTtlSeconds() != null && request.getTtlSeconds() > 0) {
            stringRedisTemplate.opsForValue().set(
                    request.getKey(), request.getValue(), request.getTtlSeconds(), TimeUnit.SECONDS);
        } else {
            stringRedisTemplate.opsForValue().set(request.getKey(), request.getValue());
        }
        log.info("redis set key={}", request.getKey());
    }

    /**
     * 删除 Redis 键。
     */
    @Override
    public void deleteRedisKey(String key) {
        requireRedis();
        assertAllowedKey(key);
        stringRedisTemplate.delete(key);
        log.info("redis del key={}", key);
    }

    private void syncRedisIfNeed(SysConfigEntity entity) {
        // 空值分支判断
        if (stringRedisTemplate == null) {
            return;
        }
        String redisKey = RedisKeyConstants.config(entity.getConfigKey());
        // 字段相等性校验
        if (Integer.valueOf(1).equals(entity.getSyncRedis())) {
            stringRedisTemplate.opsForValue().set(redisKey, entity.getConfigValue());
        } else {
            stringRedisTemplate.delete(redisKey);
        }
    }

    private void requireRedis() {
        // 空值分支判断
        if (stringRedisTemplate == null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "Redis 不可用");
        }
    }

    private static String normalizeAllowedPrefix(String prefix) {
        // 字符串非空才继续处理
        if (!StringUtils.hasText(prefix)) {
            return RedisKeyConstants.CONFIG_PREFIX;
        }
        String p = prefix.trim();
        // 遍历集合逐项处理
        for (String allow : ALLOWED_REDIS_PREFIX) {
            // 字段相等性校验
            if (p.equals(allow) || p.startsWith(allow)) {
                return p;
            }
        }
        throw new BusinessException(ErrorCode.FORBIDDEN, "仅允许 bsh:config: / bsh:dict: / bsh:stats: 前缀");
    }

    private static void assertAllowedKey(String key) {
        // 字符串非空才继续处理
        if (!StringUtils.hasText(key)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "key 不能为空");
        }
        String k = key.trim().toLowerCase(Locale.ROOT);
        // 业务条件分支
        if (k.startsWith("bsh:captcha:")) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "验证码键禁止操作");
        }
        boolean ok = ALLOWED_REDIS_PREFIX.stream().anyMatch(key::startsWith);
        // 条件不满足时走异常或跳过
        if (!ok) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "仅允许 bsh:config: / bsh:dict: / bsh:stats: 前缀");
        }
    }

    private static void apply(SysConfigEntity entity, UpsertSysConfigRequest request) {
        entity.setConfigKey(request.getConfigKey().trim());
        entity.setConfigValue(request.getConfigValue());
        entity.setValueType(StringUtils.hasText(request.getValueType())
                ? request.getValueType().trim().toUpperCase(Locale.ROOT) : "STRING");
        entity.setGroupCode(StringUtils.hasText(request.getGroupCode())
                ? request.getGroupCode().trim() : "DEFAULT");
        entity.setRemark(request.getRemark());
        entity.setSyncRedis(Boolean.TRUE.equals(request.getSyncRedis()) ? 1 : 0);
    }

    private static SysConfigDTO toDto(SysConfigEntity e) {
        SysConfigDTO dto = new SysConfigDTO();
        dto.setId(e.getId());
        dto.setConfigKey(e.getConfigKey());
        dto.setConfigValue(e.getConfigValue());
        dto.setValueType(e.getValueType());
        dto.setGroupCode(e.getGroupCode());
        dto.setRemark(e.getRemark());
        dto.setSyncRedis(Integer.valueOf(1).equals(e.getSyncRedis()));
        return dto;
    }
}
