package com.baishuhui.infrastructure.db.mapper.admin;

import com.baishuhui.domain.admin.entity.DashboardDailyEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 运营日报 Mapper。
 *
 * @author wei yz
 */
@Mapper
public interface DashboardDailyMapper extends BaseMapper<DashboardDailyEntity> {

    String COLUMNS = "id, stat_date, login_count, pending_audit, published_supply, banner_count,"
            + " audit_pie_json, category_rank_json,"
            + " create_time, create_user, create_user_name, update_time, update_user,"
            + " deleted, delete_time, area, data_year";

    @Select("SELECT " + COLUMNS
            + " FROM bsh_dashboard_daily WHERE deleted = 0 AND stat_date = #{statDate} LIMIT 1")
    DashboardDailyEntity selectByStatDate(@Param("statDate") LocalDate statDate);

    @Select("SELECT " + COLUMNS
            + " FROM bsh_dashboard_daily WHERE deleted = 0"
            + " AND stat_date >= #{from} AND stat_date <= #{to}"
            + " ORDER BY stat_date ASC")
    List<DashboardDailyEntity> selectBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
