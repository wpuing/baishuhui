package com.baishuhui.web.feign;

import feign.Client;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * 对 Feign {@link Client} 统一包装耗时日志（AGENTS §3 / §5.10）。
 *
 * @author wei yz
 */
@AutoConfiguration
@ConditionalOnClass(Client.class)
@ConditionalOnProperty(name = "bsh.feign.logging.enabled", havingValue = "true", matchIfMissing = true)
public class BshFeignLoggingAutoConfiguration {

    /**
     * 在 Feign Client 初始化后注入日志装饰，避免与 LoadBalancer Client 冲突。
     */
    @Bean
    public static BeanPostProcessor feignClientLoggingBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof Client client && !(bean instanceof LoggingFeignClient)) {
                    return new LoggingFeignClient(client);
                }
                return bean;
            }
        };
    }
}
