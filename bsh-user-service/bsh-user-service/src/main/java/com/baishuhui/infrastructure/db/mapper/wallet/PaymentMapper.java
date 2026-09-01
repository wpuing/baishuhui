package com.baishuhui.infrastructure.db.mapper.wallet;

import com.baishuhui.domain.wallet.entity.PaymentEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 支付单 Mapper。
 *
 * @author wei yz
 */
@Mapper
public interface PaymentMapper extends BaseMapper<PaymentEntity> {

    String PAYMENT_COLUMNS = """
            id, user_id, order_id, channel, amount, direction, biz_type, status,
            idempotent_key, related_payment_id,
            create_time, create_user, create_user_name, update_time, update_user,
            deleted, delete_time, area, data_year
            """;

    /**
     * 按幂等键查询支付单，用于扣款 / 退款幂等。
     *
     * @param idempotentKey 幂等键
     * @return 支付单，不存在返回 null
     */
    @Select("SELECT " + PAYMENT_COLUMNS
            + " FROM bsh_payment WHERE idempotent_key = #{idempotentKey} AND deleted = 0 LIMIT 1")
    PaymentEntity selectByIdempotentKey(@Param("idempotentKey") String idempotentKey);
}
