package com.baishuhui.interfaces.config;

import com.baishuhui.web.spi.Knife4jOpenApiCustomizer;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.stereotype.Component;

/**
 * 用户服务 OpenAPI 标题定制（SPI 示例：业务扩展不改 starter）。
 *
 * @author wei yz
 */
@Component
public class UserKnife4jOpenApiCustomizer implements Knife4jOpenApiCustomizer {

    @Override
    public void customize(io.swagger.v3.oas.models.OpenAPI openAPI) {
        Info info = openAPI.getInfo();
        if (info == null) {
            info = new Info();
            openAPI.setInfo(info);
        }
        info.setTitle("百蔬汇 - 用户服务 API");
        info.setVersion("1.0");
    }
}
