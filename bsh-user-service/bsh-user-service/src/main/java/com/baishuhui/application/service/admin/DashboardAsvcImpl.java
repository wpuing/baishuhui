package com.baishuhui.application.service.admin;

import com.baishuhui.domain.admin.entity.DashboardDailyEntity;
import com.baishuhui.domain.admin.repositories.IDashboardDailyRepository;
import com.baishuhui.infrastructure.security.InternalTokenFilter;
import com.baishuhui.interfaces.config.InternalTokenProperties;
import com.baishuhui.user.constant.UserStatusConstants;
import com.baishuhui.user.vo.admin.DashboardOverviewDTO;
import com.baishuhui.domain.user.repositories.IUserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 运营总览：当日实时聚合；历史日读日报快照；定时 upsert 到 MySQL。
 *
 * @author wei yz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardAsvcImpl implements IDashboardAsvc {

    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    private static final TypeReference<List<Map<String, Object>>> MAP_LIST_TYPE = new TypeReference<>() {
    };

    private final IVisitStatsAsvc visitStatsAsvc;

    private final IUserAuditAsvc userAuditAsvc;

    private final IUserRepository userRepository;

    private final IDashboardDailyRepository dashboardDailyRepository;

    private final ObjectMapper objectMapper;

    private final InternalTokenProperties internalTokenProperties;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${bsh.services.supply:http://127.0.0.1:8082}")
    private String supplyBase;

    @Value("${bsh.services.order:http://127.0.0.1:8083}")
    private String orderBase;

    @Value("${bsh.services.home:http://127.0.0.1:8085}")
    private String homeBase;

    /**
     * 按日聚合运营指标。
     */
    @Override
    public DashboardOverviewDTO overview(LocalDate date) {
        LocalDate target = date == null ? LocalDate.now(ZONE) : date;
        LocalDate today = LocalDate.now(ZONE);
        // 当天或未来（非法未来按今天）：实时聚合
        if (!target.isBefore(today)) {
            return buildLiveOverview(today);
        }
        return buildHistoricalOverview(target);
    }

    /**
     * 采集并落库指定日快照。
     * <p>当日：全量实时指标 upsert；历史日：仅校正登录次数，其它指标保留已有快照（避免日终任务用「次日实时值」覆盖）。
     */
    @Override
    public void snapshotDay(LocalDate date) {
        LocalDate target = date == null ? LocalDate.now(ZONE) : date;
        LocalDate today = LocalDate.now(ZONE);
        // 历史日：只校正登录次数
        if (target.isBefore(today)) {
            finalizePastDayLogin(target);
            return;
        }
        DashboardOverviewDTO live = collectLiveMetrics(target);
        DashboardDailyEntity entity = new DashboardDailyEntity();
        entity.setStatDate(target);
        entity.setLoginCount(live.getTodayVisit());
        entity.setPendingAudit(live.getPendingAudit());
        entity.setPublishedSupply(live.getPublishedSupply());
        entity.setBannerCount(live.getBannerCount());
        entity.setAuditPieJson(writeJson(live.getAuditStatusPie()));
        entity.setCategoryRankJson(writeJson(live.getCategoryRanks()));
        // 按日 upsert，便于定时覆盖刷新当日快照
        dashboardDailyRepository.saveOrUpdate(entity);
        log.info("dashboard snapshot saved date={} login={} pending={} supply={} banner={}",
                target, entity.getLoginCount(), entity.getPendingAudit(),
                entity.getPublishedSupply(), entity.getBannerCount());
    }

    /**
     * 历史日终：仅当已有快照时用登录流水校正次数；无行则不补空壳（避免前端误判完整快照）。
     */
    private void finalizePastDayLogin(LocalDate target) {
        long loginCount = visitStatsAsvc.countByDay(target);
        Optional<DashboardDailyEntity> exists = dashboardDailyRepository.findByStatDate(target);
        // 无当日小时快照则跳过，等运维补跑或次日不再虚构 0 指标
        if (exists.isEmpty()) {
            log.info("dashboard past snapshot skip (no row) date={} login={}", target, loginCount);
            return;
        }
        DashboardDailyEntity row = exists.get();
        row.setLoginCount(loginCount);
        dashboardDailyRepository.saveOrUpdate(row);
        log.info("dashboard past snapshot login refreshed date={} login={}", target, loginCount);
    }

    private DashboardOverviewDTO buildLiveOverview(LocalDate today) {
        DashboardOverviewDTO dto = collectLiveMetrics(today);
        dto.setStatDate(today.toString());
        dto.setFromSnapshot(false);
        dto.setVisitTrend(visitStatsAsvc.daysEndingAt(today, 7));
        dto.setTodayLogins(visitStatsAsvc.loginRecordsByDay(today));
        return dto;
    }

    private DashboardOverviewDTO buildHistoricalOverview(LocalDate day) {
        DashboardOverviewDTO dto = new DashboardOverviewDTO();
        dto.setStatDate(day.toString());
        long loginCount = visitStatsAsvc.countByDay(day);
        dto.setTodayVisit(loginCount);
        dto.setVisitTrend(visitStatsAsvc.daysEndingAt(day, 7));
        dto.setTodayLogins(visitStatsAsvc.loginRecordsByDay(day));

        Optional<DashboardDailyEntity> snap = dashboardDailyRepository.findByStatDate(day);
        // 有快照则回填审核/供应/Banner/图表；登录次数以流水为准更准确
        if (snap.isPresent()) {
            DashboardDailyEntity row = snap.get();
            dto.setFromSnapshot(true);
            dto.setPendingAudit(nullToZero(row.getPendingAudit()));
            dto.setPublishedSupply(nullToZero(row.getPublishedSupply()));
            dto.setBannerCount(nullToZero(row.getBannerCount()));
            dto.setAuditStatusPie(readJsonList(row.getAuditPieJson()));
            dto.setCategoryRanks(readJsonList(row.getCategoryRankJson()));
            // 快照登录作兜底（流水缺失时）
            if (loginCount <= 0L && row.getLoginCount() != null) {
                dto.setTodayVisit(row.getLoginCount());
            }
            return dto;
        }
        dto.setFromSnapshot(false);
        dto.setPendingAudit(0L);
        dto.setPublishedSupply(0L);
        dto.setBannerCount(0L);
        dto.setAuditStatusPie(List.of());
        dto.setCategoryRanks(List.of());
        log.info("dashboard historical miss snapshot date={}", day);
        return dto;
    }

    /**
     * 实时采集指标（登录按 target 日；审核/供应/Banner/排行取当前值，用于当日展示与落库）。
     */
    private DashboardOverviewDTO collectLiveMetrics(LocalDate target) {
        DashboardOverviewDTO dto = new DashboardOverviewDTO();
        dto.setTodayVisit(visitStatsAsvc.countByDay(target));
        dto.setPendingAudit(userAuditAsvc.countPendingAudit());
        dto.setAuditStatusPie(buildAuditPie());
        dto.setPublishedSupply(fetchPublishedSupplyCount());
        dto.setCategoryRanks(fetchCategoryRanks());
        dto.setBannerCount(fetchBannerCount());
        return dto;
    }

    private List<Map<String, Object>> buildAuditPie() {
        List<Map<String, Object>> pie = new ArrayList<>(4);
        // 遍历集合逐项处理
        for (String status : List.of(
                UserStatusConstants.PENDING,
                UserStatusConstants.REVIEWING,
                UserStatusConstants.APPROVED,
                UserStatusConstants.REJECTED)) {
            long c = userRepository.countByStatusAndRoles(status, List.of(
                    UserStatusConstants.ROLE_FARMER,
                    UserStatusConstants.ROLE_CONSUMER,
                    UserStatusConstants.ROLE_MERCHANT));
            Map<String, Object> row = new HashMap<>(4);
            row.put("name", status);
            row.put("value", c);
            pie.add(row);
        }
        return pie;
    }

    @SuppressWarnings("unchecked")
    private long fetchPublishedSupplyCount() {
        try {
            Map<?, ?> body = restTemplate.getForObject(supplyBase + "/api/consumer/supplies", Map.class);
            // 空值分支判断
            if (body == null) {
                return 0L;
            }
            Object data = body.get("data");
            // 业务条件分支
            if (data instanceof List<?> list) {
                return list.size();
            }
        } catch (Exception ex) {
            log.warn("dashboard supply count fail: {}", ex.getMessage());
        }
        return 0L;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> fetchCategoryRanks() {
        try {
            HttpHeaders headers = new HttpHeaders();
            String token = internalTokenProperties.getToken();
            // 内部接口须带共享令牌
            if (StringUtils.hasText(token)) {
                headers.set(InternalTokenFilter.HEADER, token);
            }
            ResponseEntity<Map> resp = restTemplate.exchange(
                    orderBase + "/internal/order/rankings/categories?topN=8",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Map.class);
            Map<?, ?> body = resp.getBody();
            // 空值分支判断
            if (body == null) {
                return List.of();
            }
            Object data = body.get("data");
            // 业务条件分支
            if (data instanceof List<?> list) {
                List<Map<String, Object>> rows = new ArrayList<>();
                // 遍历集合逐项处理
                for (Object item : list) {
                    // 业务条件分支
                    if (item instanceof Map<?, ?> m) {
                        Map<String, Object> row = new HashMap<>(4);
                        Object cat = m.get("category");
                        row.put("name", cat == null ? "未知" : String.valueOf(cat));
                        Object score = m.get("orderCount");
                        // 空值分支判断
                        if (score == null) {
                            score = m.get("tradeCount");
                        }
                        row.put("value", score == null ? 0 : score);
                        rows.add(row);
                    }
                }
                return rows;
            }
        } catch (Exception ex) {
            log.warn("dashboard category rank fail: {}", ex.getMessage());
        }
        return List.of();
    }

    @SuppressWarnings("unchecked")
    private long fetchBannerCount() {
        try {
            Map<?, ?> body = restTemplate.getForObject(homeBase + "/api/home/banners?position=HOME_TOP", Map.class);
            // 空值分支判断
            if (body == null) {
                return 0L;
            }
            Object data = body.get("data");
            // 业务条件分支
            if (data instanceof List<?> list) {
                return list.size();
            }
        } catch (Exception ex) {
            log.warn("dashboard banner count fail: {}", ex.getMessage());
        }
        return 0L;
    }

    private String writeJson(List<Map<String, Object>> rows) {
        try {
            return objectMapper.writeValueAsString(rows == null ? List.of() : rows);
        } catch (Exception ex) {
            log.warn("dashboard json write fail: {}", ex.getMessage());
            return "[]";
        }
    }

    private List<Map<String, Object>> readJsonList(String json) {
        // 字符串非空才继续处理
        if (!StringUtils.hasText(json)) {
            return List.of();
        }
        try {
            List<Map<String, Object>> rows = objectMapper.readValue(json, MAP_LIST_TYPE);
            return rows == null ? List.of() : rows;
        } catch (Exception ex) {
            log.warn("dashboard json read fail: {}", ex.getMessage());
            return List.of();
        }
    }

    private static long nullToZero(Long v) {
        return v == null ? 0L : v;
    }
}
