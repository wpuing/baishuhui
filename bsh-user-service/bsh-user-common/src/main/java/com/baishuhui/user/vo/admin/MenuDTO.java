package com.baishuhui.user.vo.admin;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单传输对象（平铺 / 树共用）。
 *
 * @author wei yz
 */
@Data
public class MenuDTO {

    private String id;

    private String parentId;

    private String clientType;

    private String name;

    private String path;

    private String icon;

    private Integer sortNo;

    private String menuType;

    private String permissionCode;

    private Integer visible;

    private List<MenuDTO> children = new ArrayList<>();
}
