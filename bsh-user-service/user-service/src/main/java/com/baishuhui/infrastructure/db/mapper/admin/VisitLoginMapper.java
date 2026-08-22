package com.baishuhui.infrastructure.db.mapper.admin;

import com.baishuhui.domain.user.entity.VisitLoginEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录访问流水 Mapper。
 *
 * @author wei yz
 */
@Mapper
public interface VisitLoginMapper extends BaseMapper<VisitLoginEntity> {

    String COLUMNS = """
            id, user_id, username, client_ip, login_time,
            create_time, create_user, create_user_name, update_time, update_user,
            deleted, delete_time, area, data_year
            """;

    /**
     * 时间窗内登录次数（含起不含止）。
     *
     * @param start 起始时间（含）
     * @param end   结束时间（不含）
     * @return 次数
     */
    @Select("""
            SELECT COUNT(1) FROM bsh_visit_login
            WHERE deleted = 0 AND login_time >= #{start} AND login_time < #{end}
            """)
    long countBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 时间窗内登录流水，最新在前。
     *
     * @param start 起始时间（含）
     * @param end   结束时间（不含）
     * @param limit 条数上限
     * @return 流水
     */
    @Select("SELECT " + COLUMNS
            + " FROM bsh_visit_login WHERE deleted = 0 AND login_time >= #{start} AND login_time < #{end}"
            + " ORDER BY login_time DESC, id DESC LIMIT #{limit}")
    List<VisitLoginEntity> selectBetween(@Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end,
                                         @Param("limit") int limit);
}
