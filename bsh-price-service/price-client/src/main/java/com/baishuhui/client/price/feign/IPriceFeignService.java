package com.baishuhui.client.price.feign;

import com.baishuhui.common.response.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 行情服务内部 Feign 契约。
 *
 * @author wei yz
 */
@FeignClient(name = "bsh-price-service", contextId = "priceFeignClient", url = "${bsh.services.price:}")
public interface IPriceFeignService {

    /**
     * 记录成交价。
     *
     * @param body sku / price / unit
     * @return 统一响应
     */
    @PostMapping("/internal/price/quotes")
    Result<Object> quote(@RequestBody Map<String, Object> body);
}
