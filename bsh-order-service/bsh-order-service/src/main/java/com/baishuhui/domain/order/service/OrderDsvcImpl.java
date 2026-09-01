package com.baishuhui.domain.order.service;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.domain.order.entity.TradeOrder;

import java.math.BigDecimal;

/**
 * 订单领域服务实现。
 *
 * @author wei yz
 */
public class OrderDsvcImpl implements IOrderDsvc {

    @Override
    public void assertParty(TradeOrder order, String operatorId) {
        // 字段相等性校验
        if (!operatorId.equals(order.getBuyerId()) && !operatorId.equals(order.getSellerId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "仅供需双方可结单");
        }
    }

    @Override
    public boolean assertCompletableOrDone(TradeOrder order) {
        // 字段相等性校验
        if (TradeOrder.COMPLETED.equals(order.getStatus())) {
            return true;
        }
        // 字段相等性校验
        if (!TradeOrder.CONFIRMED.equals(order.getStatus()) && !TradeOrder.IN_PROGRESS.equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.TRADE_STATUS_INVALID, "仅已确认/进行中订单可结单");
        }
        return false;
    }

    @Override
    public void reviseDealAmountIfPresent(TradeOrder order, BigDecimal amount) {
        // 空值分支判断
        if (amount == null) {
            return;
        }
        order.reviseDealAmount(amount);
    }

    @Override
    public void markCompleted(TradeOrder order) {
        order.complete();
    }
}
