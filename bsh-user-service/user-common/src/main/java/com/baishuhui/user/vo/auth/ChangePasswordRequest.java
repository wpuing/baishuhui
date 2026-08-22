package com.baishuhui.user.vo.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改登录密码。
 *
 * @author wei yz
 */
@Data
public class ChangePasswordRequest {

    @NotBlank
    @Size(max = 64)
    private String oldPassword;

    @NotBlank
    @Size(min = 6, max = 64)
    private String newPassword;
}
