package com.baishuhui.user.vo.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 业务用户注册请求。
 *
 * @author wei yz
 */
@Data
public class RegisterRequest {

    @NotBlank
    @Size(max = 64)
    private String username;

    @NotBlank
    @Size(min = 6, max = 64)
    private String password;

    @NotBlank
    @Size(max = 20)
    private String phone;

    /** FARMER / CONSUMER / MERCHANT */
    @NotBlank
    @Size(max = 32)
    private String role;

    @NotBlank
    @Size(max = 32)
    private String areaId;

    @Size(max = 64)
    private String nickname;

    @NotBlank
    @Size(max = 64)
    private String captchaKey;

    @NotBlank
    @Size(max = 8)
    private String captchaCode;
}
