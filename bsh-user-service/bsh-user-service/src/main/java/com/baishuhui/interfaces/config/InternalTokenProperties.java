package com.baishuhui.interfaces.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 内部服务调用共享令牌（钱包扣退款等）。
 *
 * @author wei yz
 */
@Data
@ConfigurationProperties(prefix = "bsh.internal")
public class InternalTokenProperties {

    /**
     * 调用方须在请求头 X-Bsh-Internal-Token 携带此值。
     * 生产务必通过环境变量覆盖，禁止使用默认演示值。
     */
    private String token = "bsh-local-internal-token";
}
