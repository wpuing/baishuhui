package com.baishuhui.domain.admin.entity;

import com.baishuhui.common.persistence.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 网关 IP 白名单 / 黑名单。
 *
 * @author wei yz
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bsh_ip_rule")
public class IpRuleEntity extends BaseEntity {

    private String ip;

    @TableField("rule_type")
    private String ruleType;

    private String source;

    private String reason;

    @TableField("expire_time")
    private LocalDateTime expireTime;

    @TableField("hit_count")
    private Integer hitCount;
}
