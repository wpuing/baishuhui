package com.baishuhui.user.vo.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 手工新增 IP 白名单 / 黑名单。
 *
 * @author wei yz
 */
@Data
public class UpsertIpRuleRequest {

    @NotBlank
    @Size(max = 64)
    private String ip;

    @Size(max = 256)
    private String reason;

    /** 黑名单过期分钟；空或 0 表示永久。白名单忽略。 */
    private Integer expireMinutes;
}
