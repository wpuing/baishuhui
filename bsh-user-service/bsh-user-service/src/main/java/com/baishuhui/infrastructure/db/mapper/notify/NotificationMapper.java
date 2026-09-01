package com.baishuhui.infrastructure.db.mapper.notify;

import com.baishuhui.domain.notify.entity.NotificationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 站内消息 Mapper。
 *
 * @author wei yz
 */
@Mapper
public interface NotificationMapper extends BaseMapper<NotificationEntity> {

    String COLUMNS = """
            id, user_id, msg_type, title, content, biz_type, biz_id, read_flag,
            create_time, create_user, create_user_name, update_time, update_user,
            deleted, delete_time, area, data_year
            """;

    @Select("SELECT " + COLUMNS
            + " FROM bsh_notification WHERE deleted = 0 AND user_id = #{userId}"
            + " ORDER BY create_time DESC, id DESC LIMIT #{limit}")
    List<NotificationEntity> selectByUser(@Param("userId") String userId, @Param("limit") int limit);

    @Select("""
            SELECT COUNT(1) FROM bsh_notification
            WHERE deleted = 0 AND user_id = #{userId} AND read_flag = 0
            """)
    long countUnread(@Param("userId") String userId);

    @Update("""
            UPDATE bsh_notification SET read_flag = 1, update_time = CURRENT_TIMESTAMP
            WHERE deleted = 0 AND id = #{id} AND user_id = #{userId} AND read_flag = 0
            """)
    int markRead(@Param("id") String id, @Param("userId") String userId);

    @Update("""
            UPDATE bsh_notification SET read_flag = 1, update_time = CURRENT_TIMESTAMP
            WHERE deleted = 0 AND user_id = #{userId} AND read_flag = 0
            """)
    int markAllRead(@Param("userId") String userId);
}
