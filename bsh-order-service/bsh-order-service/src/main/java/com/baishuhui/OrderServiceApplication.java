package com.baishuhui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 订单服务启动入口。
 *
 * @author wei yz
 */
@SpringBootApplication(scanBasePackages = "com.baishuhui")
@EnableDiscoveryClient
@EnableScheduling
@EnableFeignClients(basePackages = "com.baishuhui.client")
public class OrderServiceApplication {
    /**
     * 启动应用。
     */
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
