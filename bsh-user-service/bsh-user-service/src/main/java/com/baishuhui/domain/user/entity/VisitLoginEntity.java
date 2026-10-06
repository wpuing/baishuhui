package com.baishuhui.domain.user.entity;

import com.baishuhui.common.persistence.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 有效登录流水，供运营总览近 N 日登录次数统计。
 *
 * @author wei yz
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bsh_visit_login")
public class VisitLoginEntity extends BaseEntity {

    @TableField("user_id")
    private String userId;

    private String username;

    @TableField("client_ip")
    private String clientIp;

    @TableField("login_time")
    private LocalDateTime loginTime;
}
