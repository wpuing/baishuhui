package com.baishuhui.client.home.feign;

import com.baishuhui.home.vo.BannerVO;
import com.baishuhui.common.response.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 首页服务 Feign 契约。
 *
 * @author wei yz
 */
@FeignClient(name = "bsh-home-service", contextId = "homeFeignClient", url = "${bsh.services.home:}")
public interface IHomeFeignService {

    /**
     * 健康检查。
     */
    @GetMapping("/api/home/health")
    Result<String> health();

    /**
     * 按位置查询启用中的 Banner。
     *
     * @param position 位置，默认 HOME_TOP
     * @return Banner 列表
     */
    @GetMapping("/api/home/banners")
    Result<List<BannerVO>> banners(
            @RequestParam(value = "position", defaultValue = "HOME_TOP") String position);

    /**
     * 首页聚合信息流。
     *
     * @return categoryRanks / latestSupplies
     */
    @GetMapping("/api/home/feed")
    Result<Map<String, Object>> feed();
}
