package com.baishuhui.application.service.order;

import com.baishuhui.order.vo.PlaceDepositCommand;
import com.baishuhui.order.vo.TradeOrderDTO;
import com.baishuhui.client.supply.feign.ISupplyFeignService;
import com.baishuhui.supply.vo.LockSupplyCommand;
import com.baishuhui.supply.vo.UnlockSupplyCommand;
import com.baishuhui.supply.vo.SupplyInfoDTO;
import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.response.Result;
import com.baishuhui.common.util.IdUtil;
import com.baishuhui.domain.order.entity.TradeOrder;
import com.baishuhui.domain.order.repositories.ITradeOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 下定金用例：仅预定供应并生成待付定金订单，扣款在支付用例完成。
 *
 * @author wei yz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceDepositUseCase {
    private final ITradeOrderRepository tradeOrderRepository;
    private final ISupplyFeignService supplyFeignClient;

    /**
     * 先远程预定供应再落待付单；落库失败则补偿释放，避免供应永久占用。
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<TradeOrderDTO> execute(PlaceDepositCommand cmd) {
        Result<SupplyInfoDTO> supplyRes = supplyFeignClient.detail(cmd.getSupplyId());
        // Feign 失败或业务码非成功均视为供应不可用
        if (supplyRes == null || !ErrorCode.OK.equals(supplyRes.getCode()) || supplyRes.getData() == null) {
            throw new BusinessException(ErrorCode.SUPPLY_NOT_FOUND, "供应不存在");
        }
        SupplyInfoDTO supply = supplyRes.getData();

        // 禁止自买：须在预定前拦截，避免供应卡在 RESERVING
        String sellerId = supply.getMerchantId();
        if (sellerId != null && sellerId.equals(cmd.getBuyerId())) {
            throw new BusinessException(ErrorCode.INVALID_TRADE, "不能拍下自己发布的供应");
        }
        if (supply.getPrice() == null || supply.getQuantity() == null || supply.getDepositAmount() == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "供应价格/数量/定金不完整，无法下定金");
        }

        String orderId = IdUtil.nextId();
        String orderNo = "TD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + orderId.substring(0, 4).toUpperCase();

        // 先远程预定，保证同供应不会被并发定金订单抢占
        Result<SupplyInfoDTO> reserveRes = supplyFeignClient.reserve(cmd.getSupplyId(),
                LockSupplyCommand.builder().buyerId(cmd.getBuyerId()).orderId(orderId).build());
        if (reserveRes == null || !ErrorCode.OK.equals(reserveRes.getCode())) {
            throw new BusinessException(reserveRes == null ? ErrorCode.LOCK_FAILED : reserveRes.getCode(),
                    reserveRes == null ? "预定供应失败" : reserveRes.getMessage());
        }

        BigDecimal dealAmount = supply.getPrice().multiply(supply.getQuantity());
        TradeOrder order = TradeOrder.placeDeposit(
                orderId, orderNo, supply.getId(), cmd.getBuyerId(), sellerId,
                supply.getCategory(), supply.getDepositAmount(), dealAmount);
        try {
            tradeOrderRepository.save(order);
            order.pullDomainEvents();
        } catch (Exception ex) {
            // 订单未落库则释放供应，防止买家锁死他人货源
            log.error("place deposit save fail orderId={} supplyId={}", orderId, cmd.getSupplyId(), ex);
            supplyFeignClient.unlock(cmd.getSupplyId(), UnlockSupplyCommand.builder().orderId(orderId).build());
            throw new BusinessException(ErrorCode.ORDER_SAVE_FAILED, "订单落库失败已补偿释放: " + ex.getMessage());
        }
        log.info("place deposit ok orderId={} orderNo={} supplyId={} buyerId={}",
                orderId, orderNo, cmd.getSupplyId(), cmd.getBuyerId());
        return Result.success(TradeAssembler.toDTO(order));
    }
}
