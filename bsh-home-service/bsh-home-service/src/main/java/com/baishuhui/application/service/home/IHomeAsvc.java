package com.baishuhui.application.service.home;

import com.baishuhui.home.vo.BannerVO;
import com.baishuhui.order.vo.CategoryRankDTO;
import com.baishuhui.supply.vo.SupplyInfoDTO;
import com.baishuhui.common.response.Result;

import java.util.List;
import java.util.Map;

/**
 * 首页应用服务。
 *
 * @author wei yz
 */
public interface IHomeAsvc {

    /**
     * 按位置查询启用中的 Banner。
     *
     * @param position 位置
     * @return Banner 视图
     */
    List<BannerVO> listBanners(String position);

    /**
     * 品类成交排行。
     *
     * @param topN 条数
     * @return 排行
     */
    Result<List<CategoryRankDTO>> categoryRanking(int topN);

    /**
     * 首页聚合信息流。
     *
     * @return categoryRanks / latestSupplies
     */
    Map<String, Object> feed();

    /**
     * 供应列表（透传供应服务）。
     *
     * @return 供应列表
     */
    Result<List<SupplyInfoDTO>> listSupplies();
}
