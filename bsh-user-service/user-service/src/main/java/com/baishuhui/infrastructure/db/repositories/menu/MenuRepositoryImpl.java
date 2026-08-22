package com.baishuhui.infrastructure.db.repositories.menu;

import com.baishuhui.domain.menu.entity.MenuEntity;
import com.baishuhui.domain.menu.repositories.IMenuRepository;
import com.baishuhui.domain.support.PageData;
import com.baishuhui.infrastructure.db.mapper.menu.MenuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 菜单仓储实现。
 *
 * @author wei yz
 */
@Repository
@RequiredArgsConstructor
public class MenuRepositoryImpl implements IMenuRepository {

    private final MenuMapper menuMapper;

    @Override
    public PageData<MenuEntity> page(String clientType, String menuType, String keyword,
            int pageNum, int pageSize) {
        LambdaQueryWrapper<MenuEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(clientType)) {
            wrapper.eq(MenuEntity::getClientType, clientType.trim().toUpperCase(Locale.ROOT));
        }
        if (StringUtils.hasText(menuType)) {
            wrapper.eq(MenuEntity::getMenuType, menuType.trim().toUpperCase(Locale.ROOT));
        }
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like(MenuEntity::getName, kw).or().like(MenuEntity::getPath, kw)
                    .or().like(MenuEntity::getPermissionCode, kw));
        }
        wrapper.orderByAsc(MenuEntity::getClientType)
                .orderByAsc(MenuEntity::getSortNo)
                .orderByAsc(MenuEntity::getId);
        Page<MenuEntity> mpPage = menuMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        if (mpPage == null || mpPage.getRecords() == null) {
            return new PageData<>(Collections.emptyList(), 0L);
        }
        return new PageData<>(mpPage.getRecords(), mpPage.getTotal());
    }

    @Override
    public List<MenuEntity> listByClientType(String clientType) {
        LambdaQueryWrapper<MenuEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(clientType)) {
            wrapper.eq(MenuEntity::getClientType, clientType.trim().toUpperCase(Locale.ROOT));
        }
        wrapper.orderByAsc(MenuEntity::getSortNo).orderByAsc(MenuEntity::getId);
        List<MenuEntity> all = menuMapper.selectList(wrapper);
        return all == null ? Collections.emptyList() : all;
    }

    @Override
    public List<MenuEntity> listMineByUserAndClient(String userId, String clientType) {
        List<MenuEntity> list = menuMapper.selectMineByUserAndClient(userId, clientType);
        return list == null ? Collections.emptyList() : list;
    }

    @Override
    public MenuEntity getById(String id) {
        return menuMapper.selectById(id);
    }

    @Override
    public void insert(MenuEntity entity) {
        menuMapper.insert(entity);
    }

    @Override
    public int updateById(MenuEntity entity) {
        return menuMapper.updateById(entity);
    }

    @Override
    public int deleteById(String id) {
        return menuMapper.deleteById(id);
    }

    @Override
    public long countChildren(String parentId) {
        return menuMapper.countChildren(parentId);
    }

    @Override
    public List<String> listMenuIdsByRoleId(String roleId) {
        List<String> ids = menuMapper.selectMenuIdsByRoleId(roleId);
        return ids == null ? Collections.emptyList() : ids;
    }

    @Override
    public void deleteRoleMenus(String roleId) {
        menuMapper.deleteRoleMenus(roleId);
    }

    @Override
    public void insertRoleMenu(String roleId, String menuId) {
        menuMapper.insertRoleMenu(roleId, menuId);
    }
}
