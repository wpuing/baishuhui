package com.baishuhui.user.vo.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 登录请求体。
 *
 * @author wei yz
 */
@Data
public class LoginRequest {

    @NotBlank
    @Size(max = 64)
    private String username;

    @NotBlank
    @Size(max = 64)
    private String password;

    @NotBlank
    @Size(max = 64)
    private String captchaKey;

    @NotBlank
    @Size(max = 8)
    private String captchaCode;
}
