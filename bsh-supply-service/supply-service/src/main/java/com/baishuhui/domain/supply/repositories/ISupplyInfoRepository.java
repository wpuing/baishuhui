package com.baishuhui.domain.supply.repositories;

import com.baishuhui.common.ddd.IRepository;
import com.baishuhui.domain.supply.entity.SupplyInfo;

import java.util.List;

/**
 * 供应信息仓储接口。
 *
 * @author wei yz
 */
public interface ISupplyInfoRepository extends IRepository<SupplyInfo, String> {

    List<SupplyInfo> findByMerchantId(String merchantId);

    List<SupplyInfo> findPublished();

    /**
     * 公开可浏览供应（排除草稿/已下架），按发布时间倒序，带条数上限。
     */
    List<SupplyInfo> findBrowsable(int limit);

    List<SupplyInfo> findAll();
}
