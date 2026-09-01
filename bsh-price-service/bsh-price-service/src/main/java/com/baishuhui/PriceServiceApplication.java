package com.baishuhui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 价格服务启动入口。
 *
 * @author wei yz
 */
@SpringBootApplication(scanBasePackages = "com.baishuhui")
@EnableDiscoveryClient
public class PriceServiceApplication {
    /**
     * 启动应用。
     */
    public static void main(String[] args) {
        SpringApplication.run(PriceServiceApplication.class, args);
    }
}
