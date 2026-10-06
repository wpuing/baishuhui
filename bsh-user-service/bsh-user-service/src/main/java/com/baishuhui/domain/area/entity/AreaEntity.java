package com.baishuhui.domain.area.entity;

import com.baishuhui.common.persistence.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 地区表实体。
 *
 * @author wei yz
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bsh_area")
public class AreaEntity extends BaseEntity {

    /** 父级地区 id */
    private String parentId;
    /** 行政区划代码 */
    private String code;
    /** 名称 */
    private String name;
    /** 层级：1 省 / 2 市 / 3 区县 */
    private Integer level;
    /** 排序 */
    private Integer sortNo;
}
