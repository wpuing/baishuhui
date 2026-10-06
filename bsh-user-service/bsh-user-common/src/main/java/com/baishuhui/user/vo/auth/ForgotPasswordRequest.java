package com.baishuhui.user.vo.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 找回密码（校验图形验证码，无短信）。
 *
 * @author wei yz
 */
@Data
public class ForgotPasswordRequest {

    @NotBlank
    @Size(max = 64)
    private String username;

    @NotBlank
    @Size(max = 64)
    private String captchaKey;

    @NotBlank
    @Size(max = 8)
    private String captchaCode;
}
