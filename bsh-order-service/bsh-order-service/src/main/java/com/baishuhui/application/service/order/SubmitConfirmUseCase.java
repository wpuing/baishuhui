package com.baishuhui.application.service.order;

import com.baishuhui.order.vo.TradeActionCommand;
import com.baishuhui.order.vo.TradeOrderDTO;
import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.response.Result;
import com.baishuhui.domain.order.entity.TradeOrder;
import com.baishuhui.domain.order.repositories.ITradeOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 提交确认（已拍下 → 待确认）。
 *
 * @author wei yz
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubmitConfirmUseCase {

    private final ITradeOrderRepository tradeOrderRepository;

    /**
     * 买卖双方均可提交确认。
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<TradeOrderDTO> execute(TradeActionCommand cmd) {
        TradeOrder order = tradeOrderRepository.findById(cmd.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND, "订单不存在"));
        assertParty(cmd.getOperatorId(), order);
        order.submitConfirm();
        tradeOrderRepository.save(order);
        log.info("submit confirm orderId={} operatorId={} status={}",
                order.getId(), cmd.getOperatorId(), order.getStatus());
        return Result.success(TradeAssembler.toDTO(order));
    }

    private void assertParty(String operatorId, TradeOrder order) {
        // 字段相等性校验
        if (!operatorId.equals(order.getBuyerId()) && !operatorId.equals(order.getSellerId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "仅供需双方可操作");
        }
    }
}
