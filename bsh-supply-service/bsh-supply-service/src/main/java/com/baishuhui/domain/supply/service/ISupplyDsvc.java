package com.baishuhui.domain.supply.service;

import com.baishuhui.domain.supply.entity.SupplyInfo;
import com.baishuhui.domain.supply.entity.vo.ImageList;

/**
 * 供应领域服务：预定 / 锁定 / 解锁 / 结单状态迁移。
 *
 * @author wei yz
 */
public interface ISupplyDsvc {

    /**
     * 下定金预定。
     */
    void reserve(SupplyInfo supply, String buyerId, String orderId);

    /**
     * 定金支付后锁定。
     */
    void lockByDeposit(SupplyInfo supply, String buyerId, String orderId);

    /**
     * 取消或超时解锁。
     */
    void unlock(SupplyInfo supply, String orderId);

    /**
     * 结单完成或售罄。
     */
    void complete(SupplyInfo supply, ImageList completionPhotos, boolean soldOut);
}
