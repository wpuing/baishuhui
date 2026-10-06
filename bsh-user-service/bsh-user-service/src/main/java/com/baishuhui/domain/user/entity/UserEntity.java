package com.baishuhui.domain.user.entity;

import com.baishuhui.common.persistence.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户表实体。
 *
 * @author wei yz
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bsh_user")
public class UserEntity extends BaseEntity {

    private String username;
    @TableField("password_hash")
    private String passwordHash;
    private String phone;
    private String role;
    private String status;
    private String nickname;
    /** 审核备注 */
    private String auditRemark;
    /** 审核时间 */
    private java.time.LocalDateTime auditTime;
    /** 审核人 id */
    private String auditUser;
}
