package com.baishuhui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * 首页服务启动入口。
 *
 * @author wei yz
 */
@SpringBootApplication(scanBasePackages = "com.baishuhui")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.baishuhui.client")
@EnableMongoRepositories(basePackages = "com.baishuhui.infrastructure.db.mongo.repositories")
public class HomeServiceApplication {
    /**
     * 启动应用。
     */
    public static void main(String[] args) {
        SpringApplication.run(HomeServiceApplication.class, args);
    }
}
