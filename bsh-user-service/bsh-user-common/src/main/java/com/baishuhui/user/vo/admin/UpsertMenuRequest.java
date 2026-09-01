package com.baishuhui.user.vo.admin;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增 / 更新菜单入参。
 *
 * @author wei yz
 */
@Data
public class UpsertMenuRequest {

    @Size(max = 32)
    private String parentId;

    @NotBlank
    @Size(max = 16)
    private String clientType;

    @NotBlank
    @Size(max = 64)
    private String name;

    @Size(max = 256)
    private String path;

    @Size(max = 64)
    private String icon;

    @Min(0)
    @Max(99999)
    private Integer sortNo;

    @NotBlank
    @Size(max = 16)
    private String menuType;

    @Size(max = 64)
    private String permissionCode;

    /** 1 可见 / 0 隐藏；默认 1 */
    private Integer visible;
}
