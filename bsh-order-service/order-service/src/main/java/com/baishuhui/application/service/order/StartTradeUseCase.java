package com.baishuhui.application.service.order;

import com.baishuhui.order.vo.TradeActionCommand;
import com.baishuhui.order.vo.TradeOrderDTO;
import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.response.Result;
import com.baishuhui.domain.order.entity.TradeOrder;
import com.baishuhui.domain.order.repositories.ITradeOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 开始线下交易（已确认 → 进行中）。
 *
 * @author wei yz
 */
@Service
@RequiredArgsConstructor
public class StartTradeUseCase {

    private final ITradeOrderRepository tradeOrderRepository;

    /**
     * 买卖双方均可开始线下交易。
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<TradeOrderDTO> execute(TradeActionCommand cmd) {
        TradeOrder order = tradeOrderRepository.findById(cmd.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND, "订单不存在"));
        if (!cmd.getOperatorId().equals(order.getBuyerId())
                && !cmd.getOperatorId().equals(order.getSellerId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "仅供需双方可操作");
        }
        order.startTrade();
        tradeOrderRepository.save(order);
        return Result.success(TradeAssembler.toDTO(order));
    }
}
