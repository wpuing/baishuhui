package com.baishuhui.infrastructure.db.mapper.user;

import com.baishuhui.domain.user.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户表 Mapper。
 *
 * @author wei yz
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

    String USER_COLUMNS = """
            id, username, password_hash, phone, role, status, nickname,
            audit_remark, audit_time, audit_user,
            create_time, create_user, create_user_name, update_time, update_user,
            deleted, delete_time, area, data_year
            """;

    /**
     * 按登录名查询（未删除）。
     */
    @Select("SELECT " + USER_COLUMNS + " FROM bsh_user WHERE username = #{username} AND deleted = 0 LIMIT 1")
    UserEntity selectByUsername(@Param("username") String username);

    /**
     * 按手机号查询。
     */
    @Select("SELECT " + USER_COLUMNS + " FROM bsh_user WHERE phone = #{phone} AND deleted = 0 LIMIT 1")
    UserEntity selectByPhone(@Param("phone") String phone);

    /**
     * 用户角色编码列表。
     */
    @Select("""
            SELECT r.code FROM bsh_role r
            INNER JOIN bsh_user_role ur ON ur.role_id = r.id
            WHERE ur.user_id = #{userId} AND r.deleted = 0
            """)
    List<String> selectRoleCodesByUserId(@Param("userId") String userId);

    /**
     * 批量查询用户角色编码。
     */
    @Select({
            "<script>",
            "SELECT ur.user_id AS userId, r.code AS code",
            "FROM bsh_role r",
            "INNER JOIN bsh_user_role ur ON ur.role_id = r.id",
            "WHERE r.deleted = 0 AND ur.user_id IN",
            "<foreach collection='userIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>",
            "</script>"
    })
    List<UserRoleCodeRow> selectRoleCodesByUserIds(@Param("userIds") List<String> userIds);

    /**
     * 用户权限编码列表。
     */
    @Select("""
            SELECT DISTINCT p.code FROM bsh_permission p
            INNER JOIN bsh_role_permission rp ON rp.permission_id = p.id
            INNER JOIN bsh_user_role ur ON ur.role_id = rp.role_id
            WHERE ur.user_id = #{userId} AND p.deleted = 0
            """)
    List<String> selectPermissionCodesByUserId(@Param("userId") String userId);

    /**
     * 更新密码哈希。
     */
    @Update("UPDATE bsh_user SET password_hash = #{passwordHash}, update_time = CURRENT_TIMESTAMP WHERE id = #{id}")
    int updatePassword(@Param("id") String id, @Param("passwordHash") String passwordHash);

    /**
     * 按角色编码查角色 id。
     */
    @Select("SELECT id FROM bsh_role WHERE code = #{code} AND deleted = 0 LIMIT 1")
    String selectRoleIdByCode(@Param("code") String code);

    /**
     * 绑定用户角色。
     */
    @org.apache.ibatis.annotations.Insert(
            "INSERT INTO bsh_user_role (user_id, role_id) VALUES (#{userId}, #{roleId})")
    int insertUserRole(@Param("userId") String userId, @Param("roleId") String roleId);

    /**
     * 解除用户全部角色绑定。
     */
    @org.apache.ibatis.annotations.Delete("DELETE FROM bsh_user_role WHERE user_id = #{userId}")
    int deleteUserRoles(@Param("userId") String userId);

    /**
     * 待审 / 审核中数量（业务角色）。
     */
    @Select("""
            SELECT COUNT(1) FROM bsh_user
            WHERE deleted = 0 AND status IN ('PENDING', 'REVIEWING')
              AND role IN ('FARMER', 'CONSUMER', 'MERCHANT')
            """)
    long countPendingAudit();
}
