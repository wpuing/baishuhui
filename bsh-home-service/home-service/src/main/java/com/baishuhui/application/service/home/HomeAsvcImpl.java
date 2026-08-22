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
        return orderFeignClient.categoryRanking(topN);
    }

    @Override
    public Map<String, Object> feed() {
        Map<String, Object> data = new HashMap<>(4);
        Result<List<CategoryRankDTO>> ranks = orderFeignClient.categoryRanking(5);
        Result<List<SupplyInfoDTO>> supplies = supplyFeignClient.listPublished();
        data.put("categoryRanks", ranks == null ? List.of() : ranks.getData());
        data.put("latestSupplies", supplies == null ? List.of() : supplies.getData());
        return data;
    }

    @Override
    public Result<List<SupplyInfoDTO>> listSupplies() {
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
