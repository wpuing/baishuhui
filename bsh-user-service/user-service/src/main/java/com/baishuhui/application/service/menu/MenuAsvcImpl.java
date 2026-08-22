package com.baishuhui.application.service.menu;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.domain.menu.entity.MenuEntity;
import com.baishuhui.domain.menu.repositories.IMenuRepository;
import com.baishuhui.domain.support.PageData;
import com.baishuhui.domain.user.repositories.IUserRepository;
import com.baishuhui.user.vo.admin.BindRoleMenusRequest;
import com.baishuhui.user.vo.admin.MenuDTO;
import com.baishuhui.user.vo.admin.PageResultDTO;
import com.baishuhui.user.vo.admin.UpsertMenuRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 动态菜单编排：管理端 CRUD、角色绑定、端侧我的菜单。
 *
 * @author wei yz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuAsvcImpl implements IMenuAsvc {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final int MAX_PAGE_SIZE = 50;

    private static final Set<String> CLIENT_TYPES = Set.of("MERCHANT", "CONSUMER");

    private static final Set<String> MENU_TYPES = Set.of("MENU", "BUTTON");

    private final IMenuRepository menuRepository;

    private final IUserRepository userRepository;

    /**
     * 管理端分页列表。
     */
    @Override
    public PageResultDTO<MenuDTO> page(String clientType, String menuType, String keyword, int pageNum, int pageSize) {
        int num = Math.max(pageNum, 1);
        int size = pageSize <= 0 ? DEFAULT_PAGE_SIZE : Math.min(pageSize, MAX_PAGE_SIZE);
        String type = null;
        if (StringUtils.hasText(menuType)) {
            type = menuType.trim().toUpperCase(Locale.ROOT);
            if (!MENU_TYPES.contains(type)) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR, "menuType 仅支持 MENU/BUTTON");
            }
        }
        String client = StringUtils.hasText(clientType) ? clientType.trim().toUpperCase(Locale.ROOT) : null;
        PageData<MenuEntity> mpPage = menuRepository.page(client, type, keyword, num, size);
        if (mpPage.records() == null || mpPage.records().isEmpty()) {
            return PageResultDTO.of(List.of(), 0, num, size);
        }
        List<MenuDTO> records = mpPage.records().stream().map(this::toDto).toList();
        return PageResultDTO.of(records, mpPage.total(), num, size);
    }

    /**
     * 管理端树（可按端过滤）。
     */
    @Override
    public List<MenuDTO> tree(String clientType) {
        String client = StringUtils.hasText(clientType) ? clientType.trim().toUpperCase(Locale.ROOT) : null;
        List<MenuEntity> all = menuRepository.listByClientType(client);
        return buildTree(all);
    }

    /**
     * 登录用户按角色并集拉取端侧菜单树。
     */
    @Override
    public List<MenuDTO> mine(String userId, String clientType) {
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录");
        }
        String client = normalizeClient(clientType);
        List<MenuEntity> list = menuRepository.listMineByUserAndClient(userId, client);
        return buildTree(list == null ? List.of() : list);
    }

    /**
     * 新增菜单。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public MenuDTO create(UpsertMenuRequest request) {
        validate(request);
        if (StringUtils.hasText(request.getParentId())) {
            ensureParentExists(request.getParentId(), request.getClientType());
        }
        MenuEntity entity = new MenuEntity();
        apply(entity, request);
        menuRepository.insert(entity);
        log.info("menu created id={} clientType={} name={}", entity.getId(), entity.getClientType(), entity.getName());
        return toDto(entity);
    }

    /**
     * 更新菜单。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public MenuDTO update(String id, UpsertMenuRequest request) {
        validate(request);
        MenuEntity entity = menuRepository.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "菜单不存在");
        }
        if (id.equals(request.getParentId())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "父级不能是自身");
        }
        if (StringUtils.hasText(request.getParentId())) {
            ensureParentExists(request.getParentId(), request.getClientType());
        }
        apply(entity, request);
        menuRepository.updateById(entity);
        log.info("menu updated id={}", id);
        return toDto(entity);
    }

    /**
     * 删除菜单（无子节点）。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String id) {
        MenuEntity entity = menuRepository.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "菜单不存在");
        }
        if (menuRepository.countChildren(id) > 0) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "请先删除子菜单");
        }
        menuRepository.deleteById(id);
        log.info("menu deleted id={}", id);
    }

    /**
     * 按角色编码覆盖绑定菜单。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void bindRoleMenus(String roleCode, BindRoleMenusRequest request) {
        if (!StringUtils.hasText(roleCode)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "角色编码不能为空");
        }
        String roleId = userRepository.getRoleIdByCode(roleCode.trim().toUpperCase(Locale.ROOT));
        if (!StringUtils.hasText(roleId)) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "角色不存在");
        }
        List<String> menuIds = request.getMenuIds() == null ? List.of() : request.getMenuIds();
        Set<String> unique = new HashSet<>();
        for (String menuId : menuIds) {
            if (!StringUtils.hasText(menuId)) {
                continue;
            }
            if (menuRepository.getById(menuId) == null) {
                throw new BusinessException(ErrorCode.BUSINESS_ERROR, "菜单不存在: " + menuId);
            }
            unique.add(menuId);
        }
        menuRepository.deleteRoleMenus(roleId);
        for (String menuId : unique) {
            menuRepository.insertRoleMenu(roleId, menuId);
        }
        log.info("role menus bound roleCode={} count={}", roleCode, unique.size());
    }

    /**
     * 查询角色已绑定菜单 id。
     */
    @Override
    public List<String> listRoleMenuIds(String roleCode) {
        String roleId = userRepository.getRoleIdByCode(roleCode.trim().toUpperCase(Locale.ROOT));
        if (!StringUtils.hasText(roleId)) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "角色不存在");
        }
        List<String> ids = menuRepository.listMenuIdsByRoleId(roleId);
        return ids == null ? List.of() : ids;
    }

    private void validate(UpsertMenuRequest request) {
        String client = normalizeClient(request.getClientType());
        request.setClientType(client);
        String type = request.getMenuType() == null ? "" : request.getMenuType().trim().toUpperCase(Locale.ROOT);
        if (!MENU_TYPES.contains(type)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "menuType 仅支持 MENU/BUTTON");
        }
        request.setMenuType(type);
        if (request.getSortNo() == null) {
            request.setSortNo(0);
        }
        if (request.getVisible() == null) {
            request.setVisible(1);
        } else if (request.getVisible() != 0 && request.getVisible() != 1) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "visible 仅支持 0/1");
        }
    }

    private String normalizeClient(String clientType) {
        if (!StringUtils.hasText(clientType)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "clientType 不能为空");
        }
        String client = clientType.trim().toUpperCase(Locale.ROOT);
        if (!CLIENT_TYPES.contains(client)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "clientType 仅支持 MERCHANT/CONSUMER");
        }
        return client;
    }

    private void ensureParentExists(String parentId, String clientType) {
        MenuEntity parent = menuRepository.getById(parentId);
        if (parent == null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "父级菜单不存在");
        }
        if (!clientType.equalsIgnoreCase(parent.getClientType())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "父子菜单端类型须一致");
        }
    }

    private void apply(MenuEntity entity, UpsertMenuRequest request) {
        entity.setParentId(StringUtils.hasText(request.getParentId()) ? request.getParentId().trim() : null);
        entity.setClientType(request.getClientType());
        entity.setName(request.getName().trim());
        entity.setPath(StringUtils.hasText(request.getPath()) ? request.getPath().trim() : null);
        entity.setIcon(StringUtils.hasText(request.getIcon()) ? request.getIcon().trim() : null);
        entity.setSortNo(request.getSortNo());
        entity.setMenuType(request.getMenuType());
        entity.setPermissionCode(
                StringUtils.hasText(request.getPermissionCode()) ? request.getPermissionCode().trim() : null);
        entity.setVisible(request.getVisible());
    }

    private List<MenuDTO> buildTree(List<MenuEntity> all) {
        if (all == null || all.isEmpty()) {
            return List.of();
        }
        Map<String, String> parentOf = new HashMap<>(all.size() * 2);
        Map<String, MenuDTO> map = new HashMap<>(all.size() * 2);
        for (MenuEntity e : all) {
            map.put(e.getId(), toDto(e));
            parentOf.put(e.getId(), e.getParentId());
        }
        List<MenuDTO> roots = new ArrayList<>();
        for (MenuEntity e : all) {
            MenuDTO node = map.get(e.getId());
            String parentId = e.getParentId();
            if (!StringUtils.hasText(parentId) || !map.containsKey(parentId) || wouldCycle(parentOf, e.getId(), parentId)) {
                if (StringUtils.hasText(parentId) && map.containsKey(parentId)) {
                    log.warn("menu parent cycle detected id={} parentId={}", e.getId(), parentId);
                }
                roots.add(node);
            } else {
                map.get(parentId).getChildren().add(node);
            }
        }
        sortTree(roots, new HashSet<>());
        return roots;
    }

    /**
     * 从拟挂父节点沿 parent 链上溯，若碰到自身则成环。
     */
    private static boolean wouldCycle(Map<String, String> parentOf, String nodeId, String parentId) {
        String cur = parentId;
        Set<String> seen = new HashSet<>();
        while (StringUtils.hasText(cur)) {
            if (nodeId.equals(cur)) {
                return true;
            }
            if (!seen.add(cur)) {
                return true;
            }
            cur = parentOf.get(cur);
        }
        return false;
    }

    private void sortTree(List<MenuDTO> nodes, Set<String> visiting) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        nodes.sort(Comparator
                .comparing((MenuDTO m) -> m.getSortNo() == null ? 0 : m.getSortNo())
                .thenComparing(m -> m.getId() == null ? "" : m.getId()));
        for (MenuDTO n : nodes) {
            if (n.getId() != null && !visiting.add(n.getId())) {
                log.warn("menu sort cycle at id={}", n.getId());
                n.setChildren(new ArrayList<>());
                continue;
            }
            sortTree(n.getChildren(), visiting);
            if (n.getId() != null) {
                visiting.remove(n.getId());
            }
        }
    }

    private MenuDTO toDto(MenuEntity e) {
        MenuDTO dto = new MenuDTO();
        dto.setId(e.getId());
        dto.setParentId(e.getParentId());
        dto.setClientType(e.getClientType());
        dto.setName(e.getName());
        dto.setPath(e.getPath());
        dto.setIcon(e.getIcon());
        dto.setSortNo(e.getSortNo());
        dto.setMenuType(e.getMenuType());
        dto.setPermissionCode(e.getPermissionCode());
        dto.setVisible(e.getVisible());
        return dto;
    }
}
