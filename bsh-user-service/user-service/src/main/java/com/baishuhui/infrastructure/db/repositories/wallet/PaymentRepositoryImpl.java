package com.baishuhui.infrastructure.db.repositories.wallet;

import com.baishuhui.domain.wallet.entity.PaymentEntity;
import com.baishuhui.domain.wallet.repositories.IPaymentRepository;
import com.baishuhui.infrastructure.db.mapper.wallet.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 支付单仓储实现。
 *
 * @author wei yz
 */
@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements IPaymentRepository {

    private final PaymentMapper paymentMapper;

    @Override
    public PaymentEntity findByIdempotentKey(String idempotentKey) {
        return paymentMapper.selectByIdempotentKey(idempotentKey);
    }

    @Override
    public PaymentEntity getById(String id) {
        return paymentMapper.selectById(id);
    }

    @Override
    public void insert(PaymentEntity entity) {
        paymentMapper.insert(entity);
    }

    @Override
    public int updateById(PaymentEntity entity) {
        return paymentMapper.updateById(entity);
    }
}
