package com.baishuhui.gateway.ipaccess;

import com.baishuhui.gateway.ipaccess.dto.IpRuleSnapshot;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 网关内存态：白/黑名单与滑动窗口计数。
 *
 * @author wei yz
 */
@Component
public class IpAccessGuard {

    private volatile List<String> whitelist = List.of();

    private volatile List<BlacklistItem> blacklist = List.of();

    private final ConcurrentHashMap<String, WindowCounter> counters = new ConcurrentHashMap<>();

    /**
     * 用 user-service 快照替换名单。
     */
    public void replaceSnapshot(IpRuleSnapshot snapshot) {
        if (snapshot == null) {
            return;
        }
        List<String> white = snapshot.getWhitelist() == null ? List.of() : List.copyOf(snapshot.getWhitelist());
        List<BlacklistItem> black = new ArrayList<>();
        if (snapshot.getBlacklist() != null) {
            for (IpRuleSnapshot.BlacklistEntry entry : snapshot.getBlacklist()) {
                if (entry == null || entry.getIp() == null || entry.getIp().isBlank()) {
                    continue;
                }
                black.add(new BlacklistItem(entry.getIp().trim(), entry.getExpireEpochMilli()));
            }
        }
        this.whitelist = white;
        this.blacklist = List.copyOf(black);
    }

    /**
     * 是否白名单（精确或 CIDR）。
     */
    public boolean isWhitelisted(String ip) {
        for (String rule : whitelist) {
            if (IpCidrMatcher.matches(rule, ip)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否黑名单且未过期。
     */
    public boolean isBlacklisted(String ip) {
        long now = System.currentTimeMillis();
        for (BlacklistItem item : blacklist) {
            if (item.expireEpochMilli != null && item.expireEpochMilli > 0 && item.expireEpochMilli <= now) {
                continue;
            }
            if (IpCidrMatcher.matches(item.ip, ip)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 立即加入内存黑名单，避免等下一轮快照。
     */
    public void banLocal(String ip, Long expireEpochMilli) {
        List<BlacklistItem> next = new ArrayList<>(blacklist);
        next.removeIf(item -> item.ip.equalsIgnoreCase(ip));
        next.add(new BlacklistItem(ip, expireEpochMilli));
        this.blacklist = List.copyOf(next);
    }

    /**
     * 窗口内计数 +1。
     *
     * @param bucket 计数桶，如 all / auth
     * @param windowSeconds 窗口秒
     * @return 当前窗口次数
     */
    public int increment(String ip, String bucket, int windowSeconds) {
        String key = bucket + ":" + ip;
        long now = System.currentTimeMillis();
        long windowMs = Math.max(1, windowSeconds) * 1000L;
        // 偶发清理过期窗口，避免 IP 计数无限增长
        if (counters.size() > 10_000) {
            counters.entrySet().removeIf(e -> now - e.getValue().windowStart > windowMs * 2);
        }
        WindowCounter counter = counters.computeIfAbsent(key, k -> new WindowCounter(now));
        synchronized (counter) {
            if (now - counter.windowStart >= windowMs) {
                counter.windowStart = now;
                counter.count.set(0);
            }
            return counter.count.incrementAndGet();
        }
    }

    /**
     * 黑名单条目。
     */
    public record BlacklistItem(String ip, Long expireEpochMilli) {
    }

    private static final class WindowCounter {
        private long windowStart;
        private final AtomicInteger count = new AtomicInteger();

        private WindowCounter(long windowStart) {
            this.windowStart = windowStart;
        }
    }
}
