package com.baishuhui.domain.supply.service;

import com.baishuhui.domain.supply.entity.SupplyInfo;
import com.baishuhui.domain.supply.entity.vo.ImageList;

/**
 * 供应领域服务实现。
 *
 * @author wei yz
 */
public class SupplyDsvcImpl implements ISupplyDsvc {

    @Override
    public void reserve(SupplyInfo supply, String buyerId, String orderId) {
        supply.reserve(buyerId, orderId);
    }

    @Override
    public void lockByDeposit(SupplyInfo supply, String buyerId, String orderId) {
        supply.lockByDeposit(buyerId, orderId);
    }

    @Override
    public void unlock(SupplyInfo supply, String orderId) {
        supply.unlock(orderId);
    }

    @Override
    public void complete(SupplyInfo supply, ImageList completionPhotos, boolean soldOut) {
        supply.complete(completionPhotos, soldOut);
    }
}
