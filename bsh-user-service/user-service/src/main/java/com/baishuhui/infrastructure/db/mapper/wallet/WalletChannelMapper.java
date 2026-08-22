package com.baishuhui.infrastructure.db.mapper.wallet;

import com.baishuhui.domain.wallet.entity.WalletChannelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

/**
 * 钱包渠道余额 Mapper。
 *
 * @author wei yz
 */
@Mapper
public interface WalletChannelMapper extends BaseMapper<WalletChannelEntity> {

    String CHANNEL_COLUMNS = """
            id, user_id, channel, balance, version,
            create_time, create_user, create_user_name, update_time, update_user,
            deleted, delete_time, area, data_year
            """;

    /**
     * 按用户与渠道查询余额行。
     *
     * @param userId  用户 id
     * @param channel 渠道码
     * @return 余额行，不存在返回 null
     */
    @Select("SELECT " + CHANNEL_COLUMNS
            + " FROM bsh_wallet_channel WHERE user_id = #{userId} AND channel = #{channel} AND deleted = 0 LIMIT 1")
    WalletChannelEntity selectByUserAndChannel(@Param("userId") String userId, @Param("channel") String channel);

    /**
     * 用户全部渠道余额。
     *
     * @param userId 用户 id
     * @return 余额行列表
     */
    @Select("SELECT " + CHANNEL_COLUMNS
            + " FROM bsh_wallet_channel WHERE user_id = #{userId} AND deleted = 0")
    List<WalletChannelEntity> selectByUser(@Param("userId") String userId);

    /**
     * 乐观锁更新余额：版本不匹配返回 0，由调用方重试。
     *
     * @param id      余额行 id
     * @param balance 变动后余额
     * @param version 读取时的版本号
     * @return 影响行数
     */
    @Update("""
            UPDATE bsh_wallet_channel
            SET balance = #{balance}, version = version + 1, update_time = CURRENT_TIMESTAMP
            WHERE id = #{id} AND version = #{version} AND deleted = 0
            """)
    int updateBalanceOptimistic(@Param("id") String id,
                                @Param("balance") BigDecimal balance,
                                @Param("version") Integer version);
}
