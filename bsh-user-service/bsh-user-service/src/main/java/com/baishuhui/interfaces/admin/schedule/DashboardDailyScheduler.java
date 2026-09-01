package com.baishuhui.interfaces.admin.schedule;

import com.baishuhui.application.service.admin.IDashboardAsvc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * 运营日报定时快照：每小时刷新当日；每天凌晨校正昨日登录次数。
 *
 * @author wei yz
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardDailyScheduler {

    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    private final IDashboardAsvc dashboardAsvc;

    /**
     * 每小时刷新当日快照，便于中途查看「今日」落库进度。
     */
    @Scheduled(cron = "0 5 * * * ?", zone = "Asia/Shanghai")
    public void refreshTodaySnapshot() {
        LocalDate today = LocalDate.now(ZONE);
        try {
            // 定时 upsert 当日指标
            dashboardAsvc.snapshotDay(today);
        } catch (Exception ex) {
            log.error("dashboard today snapshot fail date={}", today, ex);
        }
    }

    /**
     * 每天 00:20 用登录流水校正昨日快照的登录次数（不覆盖其它日终字段）。
     */
    @Scheduled(cron = "0 20 0 * * ?", zone = "Asia/Shanghai")
    public void finalizeYesterdaySnapshot() {
        LocalDate yesterday = LocalDate.now(ZONE).minusDays(1);
        try {
            // 仅校正登录；待审/供应等保留昨日小时快照末值
            dashboardAsvc.snapshotDay(yesterday);
        } catch (Exception ex) {
            log.error("dashboard yesterday snapshot fail date={}", yesterday, ex);
        }
    }
}
