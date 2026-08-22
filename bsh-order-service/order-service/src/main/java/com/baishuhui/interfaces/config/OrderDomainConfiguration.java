package com.baishuhui.interfaces.config;

import com.baishuhui.domain.order.service.IOrderDsvc;
import com.baishuhui.domain.order.service.OrderDsvcImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 订单领域服务 Bean。
 *
 * @author wei yz
 */
@Configuration
public class OrderDomainConfiguration {

    /**
     * 注册无状态订单领域服务。
     *
     * @return IOrderDsvc
     */
    @Bean
    public IOrderDsvc orderDsvc() {
        return new OrderDsvcImpl();
    }
}
