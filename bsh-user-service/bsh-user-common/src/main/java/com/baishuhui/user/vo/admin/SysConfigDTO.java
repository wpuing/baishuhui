package com.baishuhui.user.vo.admin;

import lombok.Data;

/**
 * 系统参数 DTO。
 *
 * @author wei yz
 */
@Data
public class SysConfigDTO {

    private String id;

    private String configKey;

    private String configValue;

    private String valueType;

    private String groupCode;

    private String remark;

    private Boolean syncRedis;
}
