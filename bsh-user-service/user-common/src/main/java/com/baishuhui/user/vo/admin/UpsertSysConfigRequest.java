package com.baishuhui.user.vo.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 系统参数保存请求。
 *
 * @author wei yz
 */
@Data
public class UpsertSysConfigRequest {

    @NotBlank
    @Size(max = 128)
    private String configKey;

    @NotBlank
    @Size(max = 1024)
    private String configValue;

    @Size(max = 16)
    private String valueType;

    @Size(max = 64)
    private String groupCode;

    @Size(max = 256)
    private String remark;

    private Boolean syncRedis;
}
