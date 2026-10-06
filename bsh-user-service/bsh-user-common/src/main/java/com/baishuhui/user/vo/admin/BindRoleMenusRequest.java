package com.baishuhui.user.vo.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色绑定菜单入参。
 *
 * @author wei yz
 */
@Data
public class BindRoleMenusRequest {

    @NotNull
    private List<String> menuIds = new ArrayList<>();
}
