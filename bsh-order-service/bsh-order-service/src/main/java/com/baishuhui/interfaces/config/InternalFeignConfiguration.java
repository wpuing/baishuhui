package com.baishuhui.interfaces.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * 订单服务调用用户钱包、行情、供应内部接口时透传内部令牌。
 *
 * @author wei yz
 */
@Configuration
public class InternalFeignConfiguration {

    public static final String HEADER = "X-Bsh-Internal-Token";

    /**
     * 对钱包 / 行情 / 供应内部路径附加令牌头。
     */
    @Bean
    public RequestInterceptor internalTokenRequestInterceptor(
            @Value("${bsh.internal.token:bsh-local-internal-token}") String internalToken) {
        return template -> {
            String url = template.url();
            if (url != null
                    && (url.contains("/internal/wallet")
                    || url.contains("/internal/notifications")
                    || url.contains("/internal/price")
                    || url.contains("/internal/supply"))
                    && StringUtils.hasText(internalToken)) {
                template.header(HEADER, internalToken);
            }
        };
    }
}
