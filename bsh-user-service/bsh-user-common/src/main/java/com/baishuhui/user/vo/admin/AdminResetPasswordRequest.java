package com.baishuhui.user.vo.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理员重置用户密码。
 *
 * @author wei yz
 */
@Data
public class AdminResetPasswordRequest {

    @NotBlank
    @Size(min = 6, max = 64)
    private String newPassword;
}
