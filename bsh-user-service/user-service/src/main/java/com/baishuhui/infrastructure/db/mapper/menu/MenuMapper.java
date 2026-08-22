package com.baishuhui.infrastructure.db.mapper.menu;

import com.baishuhui.domain.menu.entity.MenuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单表 Mapper。
 *
 * @author wei yz
 */
@Mapper
public interface MenuMapper extends BaseMapper<MenuEntity> {

    /**
     * 按用户角色并集查询可见菜单（指定端）。
     */
    @Select("""
            SELECT DISTINCT m.id, m.parent_id, m.client_type, m.name, m.path, m.icon,
                   m.sort_no, m.menu_type, m.permission_code, m.visible,
                   m.create_time, m.create_user, m.create_user_name,
                   m.update_time, m.update_user, m.deleted, m.delete_time, m.area, m.data_year
            FROM bsh_menu m
            INNER JOIN bsh_role_menu rm ON rm.menu_id = m.id
            INNER JOIN bsh_user_role ur ON ur.role_id = rm.role_id
            WHERE ur.user_id = #{userId}
              AND m.client_type = #{clientType}
              AND m.deleted = 0
              AND m.visible = 1
            ORDER BY m.sort_no ASC, m.id ASC
            """)
    List<MenuEntity> selectMineByUserAndClient(
            @Param("userId") String userId, @Param("clientType") String clientType);

    /**
     * 角色已绑定菜单 id。
     */
    @Select("SELECT menu_id FROM bsh_role_menu WHERE role_id = #{roleId}")
    List<String> selectMenuIdsByRoleId(@Param("roleId") String roleId);

    /**
     * 清空角色菜单绑定。
     */
    @Delete("DELETE FROM bsh_role_menu WHERE role_id = #{roleId}")
    int deleteRoleMenus(@Param("roleId") String roleId);

    /**
     * 绑定角色菜单。
     */
    @Insert("INSERT INTO bsh_role_menu (role_id, menu_id) VALUES (#{roleId}, #{menuId})")
    int insertRoleMenu(@Param("roleId") String roleId, @Param("menuId") String menuId);

    /**
     * 统计子菜单数量（未删除）。
     */
    @Select("SELECT COUNT(1) FROM bsh_menu WHERE parent_id = #{parentId} AND deleted = 0")
    long countChildren(@Param("parentId") String parentId);
}
