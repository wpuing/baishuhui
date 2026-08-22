package com.baishuhui.interfaces.admin.controller;

import com.baishuhui.common.response.Result;
import com.baishuhui.user.vo.admin.DashboardOverviewDTO;
import com.baishuhui.user.vo.admin.VisitLoginDTO;
import com.baishuhui.application.service.admin.IDashboardAsvc;
import com.baishuhui.application.service.admin.IVisitStatsAsvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class AdminDashboardCtl {

    private final IDashboardAsvc dashboardAsvc;

    private final IVisitStatsAsvc visitStatsAsvc;

    @Operation(summary = "运营总览指标")
    @GetMapping("/overview")
    @PreAuthorize("hasAuthority('admin:view')")
    public Result<DashboardOverviewDTO> overview() {
        return Result.success(dashboardAsvc.overview());
    }

    /**
     * 今日登录访问明细（用户、IP、时间）。
     */
    @Operation(summary = "今日登录明细")
    @GetMapping("/visits")
    @PreAuthorize("hasAuthority('admin:view')")
    public Result<List<VisitLoginDTO>> visits() {
        return Result.success(visitStatsAsvc.todayLoginRecords());
    }
}
