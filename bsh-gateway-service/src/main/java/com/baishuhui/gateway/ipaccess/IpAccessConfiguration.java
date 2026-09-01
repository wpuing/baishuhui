package com.baishuhui.gateway.ipaccess;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启用 IP 访问控制配置与定时刷新。
 *
 * @author wei yz
 */
@Configuration
@EnableScheduling
@EnableConfigurationProperties(IpAccessProperties.class)
public class IpAccessConfiguration {
}
