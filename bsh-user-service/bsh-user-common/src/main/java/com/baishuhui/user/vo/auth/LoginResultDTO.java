package com.baishuhui.user.vo.auth;

import lombok.Data;

/**
 * 登录成功返回。
 *
 * @author wei yz
 */
@Data
public class LoginResultDTO {

    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private UserViewDTO user;
}
