package com.baishuhui;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 用户服务启动入口。
 *
 * @author wei yz
 */
@SpringBootApplication(scanBasePackages = "com.baishuhui")
@EnableDiscoveryClient
@EnableScheduling
@MapperScan("com.baishuhui.infrastructure.db.mapper")
public class UserServiceApplication {

    /**
     * 启动用户服务。
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
