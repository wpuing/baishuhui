package com.baishuhui.application.service.admin;

import com.baishuhui.user.vo.admin.DashboardOverviewDTO;
import com.baishuhui.user.constant.UserStatusConstants;
import com.baishuhui.domain.user.repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运营总览：本地审核/访问 + 可选拉取供应与排行。
 *
 * @author wei yz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardAsvcImpl implements IDashboardAsvc {

    private final IVisitStatsAsvc visitStatsAsvc;

    private final IUserAuditAsvc userAuditAsvc;

    private final IUserRepository userRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${bsh.services.supply:http://127.0.0.1:8082}")
    private String supplyBase;

    @Value("${bsh.services.order:http://127.0.0.1:8083}")
    private String orderBase;

    @Value("${bsh.services.home:http://127.0.0.1:8085}")
    private String homeBase;

    /**
     * 聚合运营指标。
     */
    @Override
    public DashboardOverviewDTO overview() {
        DashboardOverviewDTO dto = new DashboardOverviewDTO();
        dto.setTodayVisit(visitStatsAsvc.todayCount());
        dto.setPendingAudit(userAuditAsvc.countPendingAudit());
        dto.setVisitTrend(visitStatsAsvc.lastDays(7));
        dto.setAuditStatusPie(buildAuditPie());
        dto.setPublishedSupply(fetchPublishedSupplyCount());
        dto.setCategoryRanks(fetchCategoryRanks());
        dto.setBannerCount(fetchBannerCount());
        dto.setTodayLogins(visitStatsAsvc.todayLoginRecords());
        return dto;
    }

    private List<Map<String, Object>> buildAuditPie() {
        List<Map<String, Object>> pie = new ArrayList<>(4);
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
            if (body == null) {
                return 0L;
            }
            Object data = body.get("data");
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
            Map<?, ?> body = restTemplate.getForObject(
                    orderBase + "/internal/order/rankings/categories?topN=8", Map.class);
            if (body == null) {
                return List.of();
            }
            Object data = body.get("data");
            if (data instanceof List<?> list) {
                List<Map<String, Object>> rows = new ArrayList<>();
                for (Object item : list) {
                    if (item instanceof Map<?, ?> m) {
                        Map<String, Object> row = new HashMap<>(4);
                        Object cat = m.get("category");
                        row.put("name", cat == null ? "未知" : String.valueOf(cat));
                        Object score = m.get("orderCount");
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
            if (body == null) {
                return 0L;
            }
            Object data = body.get("data");
            if (data instanceof List<?> list) {
                return list.size();
            }
        } catch (Exception ex) {
            log.warn("dashboard banner count fail: {}", ex.getMessage());
        }
        return 0L;
    }
}
