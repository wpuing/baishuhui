package com.baishuhui.domain.admin.entity;

import com.baishuhui.common.persistence.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统参数配置。
 *
 * @author wei yz
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bsh_sys_config")
public class SysConfigEntity extends BaseEntity {

    private String configKey;

    private String configValue;

    private String valueType;

    private String groupCode;

    private String remark;

    /** 1 同步 Redis */
    private Integer syncRedis;
}
