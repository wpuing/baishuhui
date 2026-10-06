package com.baishuhui.web.spi;

import io.swagger.v3.oas.models.OpenAPI;

/**
 * Knife4j / OpenAPI 文档定制扩展点。各服务可注入 Bean 修改标题、描述等。
 *
 * @author wei yz
 */
@FunctionalInterface
public interface Knife4jOpenApiCustomizer {

    /**
     * 在默认 OpenAPI 上做增量定制。
     *
     * @param openAPI 已带默认 Info 的实例
     */
    void customize(OpenAPI openAPI);
}
