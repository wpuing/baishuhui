package com.baishuhui.user.vo.internal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 网关自动拉黑请求。
 *
 * @author wei yz
 */
@Data
public class AutoBanIpRequest {

    @NotBlank
    @Size(max = 64)
    private String ip;

    @Size(max = 256)
    private String reason;

    private Integer hitCount;

    private Integer expireMinutes;
}
