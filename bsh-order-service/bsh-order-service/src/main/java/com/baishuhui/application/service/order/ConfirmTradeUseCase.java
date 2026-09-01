package com.baishuhui.application.service.order;

import com.baishuhui.order.vo.ConfirmTradeCommand;
import com.baishuhui.order.vo.TradeOrderDTO;
import com.baishuhui.client.supply.feign.ISupplyFeignService;
import com.baishuhui.supply.vo.ConfirmSupplyCommand;
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
 * 确认交易用例。
 *
 * @author wei yz
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmTradeUseCase {
    private final ITradeOrderRepository tradeOrderRepository;
    private final ISupplyFeignService supplyFeignClient;
    private final TradeNotifyHelper tradeNotifyHelper;

    /**
     * 先远程确认供应双方，再更新本侧订单状态。
     */
    @Transactional
    public Result<TradeOrderDTO> execute(ConfirmTradeCommand cmd) {
        TradeOrder order = tradeOrderRepository.findById(cmd.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND, "订单不存在"));
        // 字段相等性校验
        if (!cmd.getOperatorId().equals(order.getBuyerId())
                && !cmd.getOperatorId().equals(order.getSellerId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "仅供需双方可确认");
        }
        log.info("feign supply confirm supplyId={} orderId={} operatorId={}",
                order.getSupplyId(), order.getId(), cmd.getOperatorId());
        Result<Void> confirmRes = supplyFeignClient.confirm(order.getSupplyId(),
                ConfirmSupplyCommand.builder().operatorId(cmd.getOperatorId()).build());
        if (confirmRes == null || !ErrorCode.OK.equals(confirmRes.getCode())) {
            log.warn("feign supply confirm fail supplyId={} orderId={} code={} msg={}",
                    order.getSupplyId(), order.getId(),
                    confirmRes == null ? null : confirmRes.getCode(),
                    confirmRes == null ? "null response" : confirmRes.getMessage());
            throw new BusinessException(confirmRes == null ? ErrorCode.CONFIRM_FAILED : confirmRes.getCode(),
                    confirmRes == null ? "确认供应失败" : confirmRes.getMessage());
        }
        order.confirm();
        tradeOrderRepository.save(order);
        tradeNotifyHelper.tradeConfirmed(order);
        log.info("trade confirmed orderId={} supplyId={} operatorId={}",
                order.getId(), order.getSupplyId(), cmd.getOperatorId());
        return Result.success(TradeAssembler.toDTO(order));
    }
}
