package com.baishuhui.web.knife4j;

import com.baishuhui.web.spi.Knife4jOpenApiCustomizer;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Knife4j / OpenAPI 自动配置。各服务可通过 {@link Knife4jOpenApiCustomizer} 覆盖标题等信息。
 *
 * @author wei yz
 */
@AutoConfiguration
public class Knife4jAutoConfiguration {

    /**
     * 默认 OpenAPI 文档；若业务已声明同名 Bean 则跳过。
     */
    @Bean
    @ConditionalOnMissingBean(OpenAPI.class)
    public OpenAPI openAPI(ObjectProvider<Knife4jOpenApiCustomizer> customizers) {
        OpenAPI openAPI = new OpenAPI().info(new Info().title("百蔬汇 API").version("1.0"));
        // 按注册顺序应用各服务定制器
        customizers.orderedStream().forEach(c -> c.customize(openAPI));
        return openAPI;
    }
}
