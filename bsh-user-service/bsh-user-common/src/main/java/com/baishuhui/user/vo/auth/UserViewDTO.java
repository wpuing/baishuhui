package com.baishuhui.user.vo.auth;

import lombok.Data;

import java.util.List;

/**
 * 登录用户视图，避免直接暴露安全实体。
 *
 * @author wei yz
 */
@Data
public class UserViewDTO {

    private String id;
    private String username;
    private String nickname;
    private String phone;
    private String role;
    private String status;
    private String area;
    private List<String> roles;
    private List<String> permissions;
}
