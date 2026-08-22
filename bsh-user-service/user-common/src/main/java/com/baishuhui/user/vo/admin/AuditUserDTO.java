package com.baishuhui.user.vo.admin;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理端用户审核列表项。
 *
 * @author wei yz
 */
@Data
public class AuditUserDTO {

    private String id;

    private String username;

    private String phone;

    private String role;

    private String status;

    private String nickname;

    private String area;

    /** 地区名称（详情展示） */
    private String areaName;

    private String auditRemark;

    private LocalDateTime auditTime;

    private String auditUser;

    private LocalDateTime createTime;

    private List<String> roles;
}
