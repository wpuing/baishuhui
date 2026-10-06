package com.baishuhui.application.service.admin;

import com.baishuhui.user.vo.admin.DashboardOverviewDTO;

import java.time.LocalDate;

/**
 * 运营总览。
 *
 * @author wei yz
 */
public interface IDashboardAsvc {

    /**
     * 指定日运营指标；date 为空则取当天实时。
     *
     * @param date 查询日（上海时区），可空
     */
    DashboardOverviewDTO overview(LocalDate date);

    /**
     * 采集并落库指定日快照（upsert）。
     *
     * @param date 统计日
     */
    void snapshotDay(LocalDate date);
}
