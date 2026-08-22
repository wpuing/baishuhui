package com.baishuhui.interfaces.home.controller;

import com.baishuhui.application.service.home.IHomeAsvc;
import com.baishuhui.home.vo.BannerVO;
import com.baishuhui.order.vo.CategoryRankDTO;
import com.baishuhui.supply.vo.SupplyInfoDTO;
import com.baishuhui.common.response.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 首页公开 REST 接口；业务编排委托 {@link IHomeAsvc}。
 *
 * @author wei yz
 */
@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeCtl {

    private final IHomeAsvc homeAsvc;

    /**
     * 健康检查。
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("home-ok");
    }

    /**
     * 按位置查询启用中的 Banner。
     */
    @GetMapping("/banners")
    public Result<List<BannerVO>> banners(
            @RequestParam(value = "position", defaultValue = "HOME_TOP") String position) {
        return Result.success(homeAsvc.listBanners(position));
    }

    /**
     * 品类成交排行。
     */
    @GetMapping("/rankings/categories")
    public Result<List<CategoryRankDTO>> categoryRanking(
            @RequestParam(value = "topN", defaultValue = "10") int topN) {
        int n = Math.min(Math.max(topN, 1), 50);
        return homeAsvc.categoryRanking(n);
    }

    /**
     * 首页聚合信息流。
     */
    @GetMapping("/feed")
    public Result<Map<String, Object>> feed() {
        return Result.success(homeAsvc.feed());
    }

    /**
     * 供应列表（透传供应服务）。
     */
    @GetMapping("/supplies")
    public Result<List<SupplyInfoDTO>> supplies() {
        return homeAsvc.listSupplies();
    }
}
