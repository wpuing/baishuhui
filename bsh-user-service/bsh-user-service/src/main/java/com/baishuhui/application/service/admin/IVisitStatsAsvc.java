package com.baishuhui.application.service.admin;

import com.baishuhui.user.vo.admin.VisitLoginDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 登录访问统计。
 *
 * @author wei yz
 */
public interface IVisitStatsAsvc {

    /**
     * 有效登录记一次访问。
     *
     * @param userId   用户 id
     * @param username 登录名
     * @param clientIp 客户端 IP
     * @return 当日登录总次数
     */
    long recordLogin(String userId, String username, String clientIp);

    /**
     * 当日登录次数。
     */
    long todayCount();

    /**
     * 指定自然日登录次数。
     *
     * @param day 日期（上海时区自然日）
     */
    long countByDay(LocalDate day);

    /**
     * 近 N 日登录次数折线（截止到今天）。
     */
    List<Map<String, Object>> lastDays(int days);

    /**
     * 以 end 为截止日的近 N 日登录折线。
     *
     * @param end  截止日（含）
     * @param days 天数
     */
    List<Map<String, Object>> daysEndingAt(LocalDate end, int days);

    /**
     * 当日登录流水。
     */
    List<VisitLoginDTO> todayLoginRecords();

    /**
     * 指定日登录流水（最新在前，最多 100 条）。
     *
     * @param day 日期
     */
    List<VisitLoginDTO> loginRecordsByDay(LocalDate day);
}
