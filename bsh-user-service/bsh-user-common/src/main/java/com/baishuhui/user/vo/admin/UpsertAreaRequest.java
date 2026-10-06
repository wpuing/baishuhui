package com.baishuhui.user.vo.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 地区新增 / 更新请求。
 *
 * @author wei yz
 */
@Data
public class UpsertAreaRequest {

    @Size(max = 32)
    private String parentId;

    @NotBlank
    @Size(max = 32)
    private String code;

    @NotBlank
    @Size(max = 64)
    private String name;

    @NotNull
    private Integer level;

    private Integer sortNo;
}
