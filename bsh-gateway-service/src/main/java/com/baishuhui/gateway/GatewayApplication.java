package com.baishuhui.gateway;

import com.baishuhui.web.BshWebAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API 网关启动入口（路由、鉴权、限流）。
 *
 * @author wei yz
 */
@SpringBootApplication(exclude = BshWebAutoConfiguration.class)
@EnableDiscoveryClient
public class GatewayApplication {

    /**
     * 启动 Spring Cloud Gateway。
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
