package com.baishuhui.interfaces.internal.controller;

import com.baishuhui.common.response.Result;
import com.baishuhui.application.service.admin.IUserAuditAsvc;
import com.baishuhui.application.service.admin.IVisitStatsAsvc;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 内部用户统计：登录去重访问、待审数量。
 *
 * @author wei yz
 */
@Hidden
@RestController
@RequestMapping("/internal/user")
@RequiredArgsConstructor
@Slf4j
public class InternalUserStatsCtl {

    private final IVisitStatsAsvc visitStatsAsvc;

    private final IUserAuditAsvc userAuditAsvc;

    /**
     * 统计快照（今日访问=当日有效登录去重人数）。
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        log.info("stats invoked");
        Map<String, Object> data = new HashMap<>(8);
        data.put("todayVisit", visitStatsAsvc.todayCount());
        data.put("pendingAudit", userAuditAsvc.countPendingAudit());
        data.put("visitTrend", visitStatsAsvc.lastDays(7));
        data.put("todayLogins", visitStatsAsvc.todayLoginRecords());
        return Result.success(data);
    }
}
