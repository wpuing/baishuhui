package com.baishuhui.user.vo.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 凭重置令牌设置新密码。
 *
 * @author wei yz
 */
@Data
public class ResetPasswordByTokenRequest {

    @NotBlank
    @Size(max = 64)
    private String resetToken;

    @NotBlank
    @Size(min = 6, max = 64)
    private String newPassword;
}
