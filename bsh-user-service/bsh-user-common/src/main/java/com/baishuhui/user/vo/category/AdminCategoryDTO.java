package com.baishuhui.user.vo.category;

import lombok.Data;

/**
 * 管理端品类项（含启停）。
 *
 * @author wei yz
 */
@Data
public class AdminCategoryDTO {

    private String id;

    private String name;

    private Integer sortNo;

    /** true 启用 */
    private Boolean enabled;
}
