package com.baishuhui.domain.menu.repositories;

import com.baishuhui.domain.menu.entity.MenuEntity;
import com.baishuhui.domain.support.PageData;

import java.util.List;

/**
 * 菜单仓储。
 *
 * @author wei yz
 */
public interface IMenuRepository {

    /**
     * 管理端分页。
     *
     * @param clientType 可选端类型
     * @param menuType   可选 MENU/BUTTON
     * @param keyword    名称/路径/权限码模糊
     * @param pageNum    页码
     * @param pageSize   每页条数
     * @return 分页数据
     */
    PageData<MenuEntity> page(String clientType, String menuType, String keyword,
            int pageNum, int pageSize);

    /**
     * 列表（可按端过滤，按排序）。
     */
    List<MenuEntity> listByClientType(String clientType);

    /**
     * 用户可见菜单（按角色并集 + 端）。
     */
    List<MenuEntity> listMineByUserAndClient(String userId, String clientType);

    /**
     * 按主键查询。
     */
    MenuEntity getById(String id);

    /**
     * 新增。
     */
    void insert(MenuEntity entity);

    /**
     * 更新。
     */
    int updateById(MenuEntity entity);

    /**
     * 逻辑删除。
     */
    int deleteById(String id);

    /**
     * 子菜单数量。
     */
    long countChildren(String parentId);

    /**
     * 角色已绑定菜单 id。
     */
    List<String> listMenuIdsByRoleId(String roleId);

    /**
     * 清空角色菜单绑定。
     */
    void deleteRoleMenus(String roleId);

    /**
     * 绑定角色菜单。
     */
    void insertRoleMenu(String roleId, String menuId);
}
