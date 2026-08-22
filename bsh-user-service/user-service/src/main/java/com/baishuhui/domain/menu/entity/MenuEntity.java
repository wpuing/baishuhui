package com.baishuhui.domain.menu.entity;

import com.baishuhui.common.persistence.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单表实体。
 *
 * @author wei yz
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bsh_menu")
public class MenuEntity extends BaseEntity {

    /** 父级菜单 id，根为空 */
    private String parentId;

    /** 端类型：MERCHANT / CONSUMER */
    private String clientType;

    /** 显示名称 */
    private String name;

    /** 路由或页面路径 */
    private String path;

    /** 图标标识 */
    private String icon;

    /** 排序号，越小越靠前 */
    private Integer sortNo;

    /** 菜单类型：MENU / BUTTON */
    private String menuType;

    /** 权限码，可空 */
    private String permissionCode;

    /** 是否可见：1 可见 / 0 隐藏 */
    private Integer visible;
}
