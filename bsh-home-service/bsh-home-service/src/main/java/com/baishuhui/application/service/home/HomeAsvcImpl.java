package com.baishuhui.application.service.home;

import com.baishuhui.home.vo.BannerVO;
import com.baishuhui.client.order.feign.IOrderFeignService;
import com.baishuhui.order.vo.CategoryRankDTO;
import com.baishuhui.client.supply.feign.ISupplyFeignService;
import com.baishuhui.supply.vo.SupplyInfoDTO;
import com.baishuhui.common.response.Result;
import com.baishuhui.domain.home.entity.Banner;
import com.baishuhui.domain.home.repositories.IBannerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 首页应用服务：编排 Banner、排行与供应聚合。
 *
 * @author wei yz
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HomeAsvcImpl implements IHomeAsvc {

    private final IOrderFeignService orderFeignClient;
    private final ISupplyFeignService supplyFeignClient;
    private final IBannerRepository bannerRepository;

    @Override
    public List<BannerVO> listBanners(String position) {
        return bannerRepository.listEnabledByPosition(position).stream()
                .map(this::toVo)
                .collect(Collectors.toList());
    }

    @Override
    public Result<List<CategoryRankDTO>> categoryRanking(int topN) {
        // 跨服务：订单服务统计品类成交排行
        log.info("feign order categoryRanking topN={}", topN);
        return orderFeignClient.categoryRanking(topN);
    }

    @Override
    public Map<String, Object> feed() {
        Map<String, Object> data = new HashMap<>(4);
        // 跨服务：并行拉取排行与最新供应供首页聚合
        log.info("feign home feed aggregate");
        Result<List<CategoryRankDTO>> ranks = orderFeignClient.categoryRanking(5);
        Result<List<SupplyInfoDTO>> supplies = supplyFeignClient.listPublished();
        data.put("categoryRanks", ranks == null ? List.of() : ranks.getData());
        data.put("latestSupplies", supplies == null ? List.of() : supplies.getData());
        return data;
    }

    @Override
    public Result<List<SupplyInfoDTO>> listSupplies() {
        // 跨服务：供应服务全量列表
        log.info("feign supply listAll");
        return supplyFeignClient.listAll();
    }

    private BannerVO toVo(Banner banner) {
        BannerVO vo = new BannerVO();
        vo.setId(banner.getId());
        vo.setTitle(banner.getTitle());
        vo.setImageUrl(banner.getImageUrl());
        vo.setLinkUrl(banner.getLinkUrl());
        vo.setPosition(banner.getPosition());
        vo.setWeight(banner.getWeight());
        vo.setEnabled(banner.getEnabled());
        return vo;
    }
}
