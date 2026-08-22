package com.baishuhui.domain.order.repositories;

import com.baishuhui.common.ddd.IRepository;
import com.baishuhui.domain.order.entity.TradeOrder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 交易订单仓储接口。
 *
 * @author wei yz
 */
public interface ITradeOrderRepository extends IRepository<TradeOrder, String> {

    List<TradeOrder> findCompleted();

    List<TradeOrder> findBySupplyId(String supplyId);

    /**
     * 查询待付定金且已超时的订单（定时释放供应用）。
     */
    List<TradeOrder> findExpiredDepositPending(LocalDateTime now);

    /**
     * 仅当仍为待付定金时原子取消，返回是否成功；防止超时任务覆盖已支付订单。
     */
    boolean tryCancelExpiredPending(String orderId, LocalDateTime cancelledAt);

    /**
     * 买家维度分页。
     */
    List<TradeOrder> pageByBuyer(String buyerId, String status, int pageNum, int pageSize);

    long countByBuyer(String buyerId, String status);

    /**
     * 卖家维度分页（可按状态 / 品类 / 关键词 / 时间）。
     */
    List<TradeOrder> pageBySeller(
            String sellerId,
            String status,
            String category,
            String keyword,
            LocalDateTime fromInclusive,
            LocalDateTime toExclusive,
            int pageNum,
            int pageSize);

    long countBySeller(
            String sellerId,
            String status,
            String category,
            String keyword,
            LocalDateTime fromInclusive,
            LocalDateTime toExclusive);

    /**
     * 卖家筛选列表（图表聚合用，限制条数）。
     */
    List<TradeOrder> listBySeller(
            String sellerId,
            String status,
            String category,
            String keyword,
            LocalDateTime fromInclusive,
            LocalDateTime toExclusive,
            int limit);
}
