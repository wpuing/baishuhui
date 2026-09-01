package com.baishuhui.infrastructure.db.mapper.wallet;

import com.baishuhui.domain.wallet.entity.WalletLedgerEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 钱包流水 Mapper。
 *
 * @author wei yz
 */
@Mapper
public interface WalletLedgerMapper extends BaseMapper<WalletLedgerEntity> {

    String LEDGER_COLUMNS = """
            id, user_id, channel, biz_type, direction, amount, balance_before, balance_after,
            order_id, payment_id, remark,
            create_time, create_user, create_user_name, update_time, update_user,
            deleted, delete_time, area, data_year
            """;

    /**
     * 用户某业务类型的流水条数，用于赠送等一次性动作防重。
     *
     * @param userId  用户 id
     * @param bizType 业务类型
     * @return 条数
     */
    @Select("""
            SELECT COUNT(1) FROM bsh_wallet_ledger
            WHERE user_id = #{userId} AND biz_type = #{bizType} AND deleted = 0
            """)
    long countByUserAndBizType(@Param("userId") String userId, @Param("bizType") String bizType);

    /**
     * 用户最近流水（倒序）。
     *
     * @param userId 用户 id
     * @param limit  条数上限
     * @return 流水列表
     */
    @Select("SELECT " + LEDGER_COLUMNS
            + " FROM bsh_wallet_ledger WHERE user_id = #{userId} AND deleted = 0"
            + " ORDER BY create_time DESC, id DESC LIMIT #{limit}")
    List<WalletLedgerEntity> selectRecentByUser(@Param("userId") String userId, @Param("limit") int limit);
}
