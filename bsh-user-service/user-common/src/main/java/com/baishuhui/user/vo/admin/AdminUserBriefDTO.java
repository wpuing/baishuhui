package com.baishuhui.user.vo.admin;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理端用户简要信息。
 *
 * @author wei yz
 */
@Data
public class AdminUserBriefDTO {

    private String id;
    private String username;
    private String phone;
    private String role;
    private String status;
    private String nickname;
    private LocalDateTime createTime;
    private String createUser;
    private Integer dataYear;
    private List<String> roles;
}
