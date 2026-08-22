package com.baishuhui.interfaces.config;

import com.baishuhui.domain.supply.service.ISupplyDsvc;
import com.baishuhui.domain.supply.service.SupplyDsvcImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 供应领域服务 Bean。
 *
 * @author wei yz
 */
@Configuration
public class SupplyDomainConfiguration {

    /**
     * 注册无状态供应领域服务。
     *
     * @return ISupplyDsvc
     */
    @Bean
    public ISupplyDsvc supplyDsvc() {
        return new SupplyDsvcImpl();
    }
}
