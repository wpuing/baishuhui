package com.baishuhui.user.vo.admin;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 运营总览聚合。
 *
 * @author wei yz
 */
@Data
public class DashboardOverviewDTO {

    /** 统计日 yyyy-MM-dd（上海时区） */
    private String statDate;

    /** 是否来自日报快照（历史日） */
    private boolean fromSnapshot;

    /** 当日登录次数（字段名兼容前端 todayVisit） */
    private long todayVisit;

    private long pendingAudit;

    private long publishedSupply;

    private long bannerCount;

    private List<Map<String, Object>> visitTrend = new ArrayList<>();

    private List<Map<String, Object>> auditStatusPie = new ArrayList<>();

    private List<Map<String, Object>> categoryRanks = new ArrayList<>();

    /** 当日登录明细（用户、IP、时间） */
    private List<VisitLoginDTO> todayLogins = new ArrayList<>();
}
