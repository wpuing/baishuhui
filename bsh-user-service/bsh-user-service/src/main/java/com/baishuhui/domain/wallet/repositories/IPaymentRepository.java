package com.baishuhui.domain.wallet.repositories;

import com.baishuhui.domain.wallet.entity.PaymentEntity;

/**
 * 支付单仓储。
 *
 * @author wei yz
 */
public interface IPaymentRepository {

    /**
     * 按幂等键查询。
     */
    PaymentEntity findByIdempotentKey(String idempotentKey);

    /**
     * 按主键查询。
     */
    PaymentEntity getById(String id);

    /**
     * 新增支付单。
     */
    void insert(PaymentEntity entity);

    /**
     * 按主键更新。
     */
    int updateById(PaymentEntity entity);
}
