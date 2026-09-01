package com.baishuhui.client.order.feign;

import com.baishuhui.order.vo.CategoryRankDTO;
import com.baishuhui.common.response.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 订单服务 Feign 客户端契约。
 *
 * @author wei yz
 */
@FeignClient(name = "bsh-order-service", contextId = "orderFeignClient", url = "${bsh.services.order:}")
public interface IOrderFeignService {

    @GetMapping("/internal/order/rankings/categories")
    Result<List<CategoryRankDTO>> categoryRanking(@RequestParam(value = "topN", defaultValue = "10") int topN);
}
