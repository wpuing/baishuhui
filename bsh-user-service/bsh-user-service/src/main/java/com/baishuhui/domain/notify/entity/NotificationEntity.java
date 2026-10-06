package com.baishuhui.domain.notify.entity;

import com.baishuhui.common.persistence.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 站内消息实体。
 *
 * @author wei yz
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bsh_notification")
public class NotificationEntity extends BaseEntity {

    @TableField("user_id")
    private String userId;

    @TableField("msg_type")
    private String msgType;

    private String title;

    private String content;

    @TableField("biz_type")
    private String bizType;

    @TableField("biz_id")
    private String bizId;

    /** 0 未读 / 1 已读 */
    @TableField("read_flag")
    private Integer readFlag;
}
