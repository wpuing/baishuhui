package com.baishuhui.application.service.menu;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.user.vo.admin.BindRoleMenusRequest;
import com.baishuhui.user.vo.admin.MenuDTO;
import com.baishuhui.user.vo.admin.PageResultDTO;
import com.baishuhui.user.vo.admin.UpsertMenuRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 应用服务接口（原 IMenuAsvc）。
 *
 * @author wei yz
 */
public interface IMenuAsvc {
    PageResultDTO<MenuDTO> page(String clientType, String menuType, String keyword, int pageNum, int pageSize);
    List<MenuDTO> tree(String clientType);
    List<MenuDTO> mine(String userId, String clientType);
    MenuDTO create(UpsertMenuRequest request);
    MenuDTO update(String id, UpsertMenuRequest request);
    void delete(String id);
    void bindRoleMenus(String roleCode, BindRoleMenusRequest request);
    List<String> listRoleMenuIds(String roleCode);
}
