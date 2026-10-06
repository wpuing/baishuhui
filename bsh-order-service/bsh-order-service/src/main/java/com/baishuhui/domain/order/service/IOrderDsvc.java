package com.baishuhui.domain.order.service;

import com.baishuhui.domain.order.entity.TradeOrder;

import java.math.BigDecimal;

/**
 * 订单领域服务：结单前校验、改成交额、标记完成等规则。
 *
 * @author wei yz
 */
public interface IOrderDsvc {

    /**
     * 校验操作人是否为买卖双方。
     *
     * @param order      订单
     * @param operatorId 操作人
     */
    void assertParty(TradeOrder order, String operatorId);

    /**
     * 校验是否允许结单（已确认 / 进行中；已结单视为幂等完成）。
     *
     * @param order 订单
     * @return true 表示已是结单状态，可直接返回
     */
    boolean assertCompletableOrDone(TradeOrder order);

    /**
     * 结单前修订成交额（amount 为空则跳过）。
     *
     * @param order  订单
     * @param amount 新成交额
     */
    void reviseDealAmountIfPresent(TradeOrder order, BigDecimal amount);

    /**
     * 标记订单已结单。
     *
     * @param order 订单
     */
    void markCompleted(TradeOrder order);
}
