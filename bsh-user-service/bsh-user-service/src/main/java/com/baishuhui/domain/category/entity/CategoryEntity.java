package com.baishuhui.domain.category.entity;

import com.baishuhui.common.persistence.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 农产品品类字典。
 *
 * @author wei yz
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bsh_category")
public class CategoryEntity extends BaseEntity {

    /** 品类名称 */
    private String name;

    /** 排序，越小越靠前 */
    private Integer sortNo;

    /** 1 启用 / 0 停用 */
    private Integer enabled;
}
