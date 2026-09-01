package com.baishuhui.application.service.admin;

import com.baishuhui.domain.user.entity.VisitLoginEntity;
import com.baishuhui.domain.user.repositories.IVisitLoginRepository;
import com.baishuhui.infrastructure.cache.RedisKeyConstants;
import com.baishuhui.user.vo.admin.VisitLoginDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 今日访问：每次有效登录计 1 次（token 过期后重新登录再计 1 次），记录登录时间与 IP。
 * 近 N 日次数以 MySQL {@code bsh_visit_login} 为准，避免 Redis 次日过期导致折线全 0。
 *
 * @author wei yz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VisitStatsAsvcImpl implements IVisitStatsAsvc {

    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final int MAX_LOG_KEEP = 500;

    /** 计数缓存保留天数，须覆盖运营总览近 7 日折线 */
    private static final Duration COUNT_TTL = Duration.ofDays(10);

    private final IVisitLoginRepository visitLoginRepository;

    private final ConcurrentHashMap<String, AtomicLong> localCount = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, CopyOnWriteArrayList<String>> localLog = new ConcurrentHashMap<>();

    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 有效登录记一次访问（不去重；同用户多次登录多次计入）。
     *
     * @param userId   用户 id
     * @param username 登录名
     * @param clientIp 客户端 IP
     * @return 当日登录总次数
     */
    @Override
    public long recordLogin(String userId, String username, String clientIp) {
        // 字符串非空才继续处理
        if (!StringUtils.hasText(userId)) {
            return todayCount();
        }
        LocalDateTime now = LocalDateTime.now(ZONE);
        String day = now.toLocalDate().format(DAY);
        String ip = StringUtils.hasText(clientIp) ? clientIp.trim() : "-";
        String name = StringUtils.hasText(username) ? username.trim() : userId;
        persistMysql(userId, name, ip, now);
        cacheRedis(day, name, ip, now.format(TIME), userId);
        long total = localCount.computeIfAbsent(day, d -> new AtomicLong(0)).incrementAndGet();
        CopyOnWriteArrayList<String> logs = localLog.computeIfAbsent(day, d -> new CopyOnWriteArrayList<>());
        logs.add(0, name + "|" + ip + "|" + now.format(TIME) + "|" + userId);
        // 循环处理
        while (logs.size() > MAX_LOG_KEEP) {
            logs.remove(logs.size() - 1);
        }
        log.info("visit login +1 userId={} ip={} day={} mysqlDayCount={}", userId, ip, day, dayCount(day));
        return dayCount(day);
    }

    /**
     * 当日登录次数。
     */
    @Override
    public long todayCount() {
        return dayCount(LocalDate.now(ZONE).format(DAY));
    }

    /**
     * 指定自然日登录次数。
     */
    @Override
    public long countByDay(LocalDate day) {
        // 空值分支判断
        if (day == null) {
            return todayCount();
        }
        return dayCount(day.format(DAY));
    }

    /**
     * 近 N 日登录次数折线（截止今天）。
     */
    @Override
    public List<Map<String, Object>> lastDays(int days) {
        return daysEndingAt(LocalDate.now(ZONE), days);
    }

    /**
     * 以 end 为截止日的近 N 日登录折线。
     */
    @Override
    public List<Map<String, Object>> daysEndingAt(LocalDate end, int days) {
        int n = Math.max(1, Math.min(days, 30));
        LocalDate endDay = end == null ? LocalDate.now(ZONE) : end;
        List<Map<String, Object>> list = new ArrayList<>(n);
        // 循环处理
        for (int i = n - 1; i >= 0; i--) {
            LocalDate d = endDay.minusDays(i);
            String day = d.format(DAY);
            Map<String, Object> row = new LinkedHashMap<>(4);
            row.put("day", day);
            row.put("label", d.toString());
            row.put("count", dayCount(day));
            list.add(row);
        }
        return list;
    }

    /**
     * 当日登录流水（含 IP、时间），最新在前，最多 100 条。
     */
    @Override
    public List<VisitLoginDTO> todayLoginRecords() {
        return loginRecordsByDay(LocalDate.now(ZONE));
    }

    /**
     * 指定日登录流水。
     */
    @Override
    public List<VisitLoginDTO> loginRecordsByDay(LocalDate day) {
        LocalDate target = day == null ? LocalDate.now(ZONE) : day;
        LocalDateTime start = target.atStartOfDay();
        LocalDateTime end = target.plusDays(1).atStartOfDay();
        try {
            List<VisitLoginEntity> rows = visitLoginRepository.listBetween(start, end, 100);
            // 空值分支判断
            if (rows != null && !rows.isEmpty()) {
                List<VisitLoginDTO> list = new ArrayList<>(rows.size());
                // 遍历集合逐项处理
                for (VisitLoginEntity row : rows) {
                    list.add(toDto(row));
                }
                return list;
            }
        } catch (Exception ex) {
            log.warn("visit log mysql fail day={}: {}", target, ex.getMessage());
        }
        // 仅当天可回退 Redis/本地缓存
        if (target.equals(LocalDate.now(ZONE))) {
            return todayLoginRecordsFallback();
        }
        return List.of();
    }

    private void persistMysql(String userId, String name, String ip, LocalDateTime now) {
        try {
            VisitLoginEntity row = new VisitLoginEntity();
            row.setUserId(userId);
            row.setUsername(name);
            row.setClientIp(ip);
            row.setLoginTime(now);
            visitLoginRepository.insert(row);
        } catch (Exception ex) {
            log.warn("visit login mysql fail userId={}: {}", userId, ex.getMessage());
        }
    }

    private void cacheRedis(String day, String name, String ip, String loginTime, String userId) {
        // 空值分支判断
        if (stringRedisTemplate == null) {
            return;
        }
        try {
            String countKey = RedisKeyConstants.visitCount(day);
            String logKey = RedisKeyConstants.visitLog(day);
            stringRedisTemplate.opsForValue().increment(countKey);
            stringRedisTemplate.expire(countKey, COUNT_TTL);
            stringRedisTemplate.opsForList().leftPush(logKey, name + "|" + ip + "|" + loginTime + "|" + userId);
            stringRedisTemplate.opsForList().trim(logKey, 0, MAX_LOG_KEEP - 1L);
            stringRedisTemplate.expire(logKey, COUNT_TTL);
        } catch (Exception ex) {
            log.warn("visit login redis fail: {}", ex.getMessage());
        }
    }

    private long dayCount(String day) {
        long mysqlCount = 0L;
        try {
            LocalDate d = LocalDate.parse(day, DAY);
            mysqlCount = visitLoginRepository.countBetween(d.atStartOfDay(), d.plusDays(1).atStartOfDay());
        } catch (Exception ex) {
            log.warn("visit count mysql fail day={}: {}", day, ex.getMessage());
        }
        long redisCount = 0L;
        // 空值分支判断
        if (stringRedisTemplate != null) {
            try {
                String v = stringRedisTemplate.opsForValue().get(RedisKeyConstants.visitCount(day));
                // 空值分支判断
                if (v != null) {
                    redisCount = Long.parseLong(v);
                }
            } catch (Exception ex) {
                log.warn("visit count redis fail: {}", ex.getMessage());
            }
        }
        AtomicLong local = localCount.get(day);
        long localVal = local == null ? 0L : local.get();
        return Math.max(mysqlCount, Math.max(redisCount, localVal));
    }

    private List<VisitLoginDTO> todayLoginRecordsFallback() {
        String day = LocalDate.now(ZONE).format(DAY);
        List<String> raw = new ArrayList<>();
        // 空值分支判断
        if (stringRedisTemplate != null) {
            try {
                List<String> fromRedis = stringRedisTemplate.opsForList()
                        .range(RedisKeyConstants.visitLog(day), 0, 99);
                // 空值分支判断
                if (fromRedis != null) {
                    raw.addAll(fromRedis);
                }
            } catch (Exception ex) {
                log.warn("visit log redis fail: {}", ex.getMessage());
            }
        }
        // 集合为空则跳过
        if (raw.isEmpty()) {
            CopyOnWriteArrayList<String> local = localLog.get(day);
            // 空值分支判断
            if (local != null) {
                int end = Math.min(100, local.size());
                raw.addAll(local.subList(0, end));
            }
        }
        List<VisitLoginDTO> list = new ArrayList<>(raw.size());
        // 遍历集合逐项处理
        for (String line : raw) {
            list.add(parseLine(line));
        }
        return list;
    }

    private static VisitLoginDTO toDto(VisitLoginEntity row) {
        VisitLoginDTO dto = new VisitLoginDTO();
        dto.setUserId(row.getUserId());
        dto.setUsername(row.getUsername());
        dto.setIp(row.getClientIp());
        dto.setLoginTime(row.getLoginTime() == null ? "" : row.getLoginTime().format(TIME));
        return dto;
    }

    private static VisitLoginDTO parseLine(String raw) {
        String[] parts = raw == null ? new String[0] : raw.split("\\|", 4);
        VisitLoginDTO row = new VisitLoginDTO();
        row.setUsername(parts.length > 0 ? parts[0] : "-");
        row.setIp(parts.length > 1 ? parts[1] : "-");
        row.setLoginTime(parts.length > 2 ? parts[2] : "");
        row.setUserId(parts.length > 3 ? parts[3] : "");
        return row;
    }
}
