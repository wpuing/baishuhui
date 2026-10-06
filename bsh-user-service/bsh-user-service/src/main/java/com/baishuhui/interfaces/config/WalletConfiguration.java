package com.baishuhui.interfaces.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 启用钱包配置绑定。
 *
 * @author wei yz
 */
@Configuration
@EnableConfigurationProperties({WalletProperties.class, InternalTokenProperties.class})
public class WalletConfiguration {
}
