package com.baishuhui.user.vo.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 品类新增 / 更新请求。
 *
 * @author wei yz
 */
@Data
public class UpsertCategoryRequest {

    @NotBlank
    @Size(max = 64)
    private String name;

    private Integer sortNo;

    /** 是否启用，默认 true */
    @NotNull
    private Boolean enabled;
}
