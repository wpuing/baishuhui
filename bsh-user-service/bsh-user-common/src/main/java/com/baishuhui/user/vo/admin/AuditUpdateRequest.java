package com.baishuhui.user.vo.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理端修改业务用户资料。
 *
 * @author wei yz
 */
@Data
public class AuditUpdateRequest {

    @NotBlank(message = "昵称不能为空")
    @Size(max = 64)
    private String nickname;

    @NotBlank(message = "手机号不能为空")
    @Size(max = 20)
    private String phone;

    @Size(max = 32)
    private String areaId;
}
