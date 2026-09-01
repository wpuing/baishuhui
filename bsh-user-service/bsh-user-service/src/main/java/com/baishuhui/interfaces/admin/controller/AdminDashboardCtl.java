package com.baishuhui.interfaces.admin.controller;

import com.baishuhui.common.response.Result;
import com.baishuhui.user.vo.admin.DashboardOverviewDTO;
import com.baishuhui.user.vo.admin.VisitLoginDTO;
import com.baishuhui.application.service.admin.IDashboardAsvc;
import com.baishuhui.application.service.admin.IVisitStatsAsvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * 运营总览。
 *
 * @author wei yz
 */
@Tag(name = "管理端-运营总览")
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardCtl {

    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    private final IDashboardAsvc dashboardAsvc;

    private final IVisitStatsAsvc visitStatsAsvc;

    @Operation(summary = "运营总览指标（可按日）")
    @GetMapping("/overview")
    @PreAuthorize("hasAuthority('admin:view')")
    public Result<DashboardOverviewDTO> overview(
            @Parameter(description = "统计日 yyyy-MM-dd，缺省为今天")
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate target = resolveDate(date);
        log.info("overview invoked date={}", target);
        return Result.success(dashboardAsvc.overview(target));
    }

    /**
     * 指定日登录访问明细（用户、IP、时间）。
     */
    @Operation(summary = "登录明细（可按日）")
    @GetMapping("/visits")
    @PreAuthorize("hasAuthority('admin:view')")
    public Result<List<VisitLoginDTO>> visits(
            @Parameter(description = "统计日 yyyy-MM-dd，缺省为今天")
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate target = resolveDate(date);
        log.info("visits invoked date={}", target);
        return Result.success(visitStatsAsvc.loginRecordsByDay(target));
    }

    private static LocalDate resolveDate(LocalDate date) {
        LocalDate today = LocalDate.now(ZONE);
        // 空或未来日一律按今天
        if (date == null || date.isAfter(today)) {
            return today;
        }
        return date;
    }
}
