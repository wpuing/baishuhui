package com.baishuhui.application.service.supply;

import com.baishuhui.application.service.warehouse.IWarehouseAsvc;
import com.baishuhui.supply.vo.CompleteSupplyCommand;
import com.baishuhui.supply.vo.ConfirmSupplyCommand;
import com.baishuhui.supply.vo.LockSupplyCommand;
import com.baishuhui.supply.vo.PublishSupplyCommand;
import com.baishuhui.supply.vo.SupplyOwnerCommand;
import com.baishuhui.supply.vo.UnlockSupplyCommand;
import com.baishuhui.supply.vo.UpdateSupplyCommand;
import com.baishuhui.supply.vo.SupplyInfoDTO;
import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.response.Result;
import com.baishuhui.domain.supply.entity.SupplyInfo;
import com.baishuhui.domain.supply.entity.vo.ImageList;
import com.baishuhui.domain.supply.entity.vo.ProductSpec;
import com.baishuhui.domain.supply.repositories.ISupplyInfoRepository;
import com.baishuhui.domain.supply.service.ISupplyDsvc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 供应应用服务编排入口。
 *
 * @author wei yz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SupplyAsvcImpl implements ISupplyAsvc {
    private final PublishSupplyUseCase publishSupplyUseCase;
    private final ISupplyInfoRepository supplyInfoRepository;
    private final ISupplyDsvc supplyDsvc;
    private final IWarehouseAsvc warehouseAsvc;

    /**
     * 发布供应。
     */
    @Override
    public Result<SupplyInfoDTO> publishSupply(PublishSupplyCommand cmd) {
        return publishSupplyUseCase.execute(cmd);
    }

    /**
     * 改货。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<SupplyInfoDTO> updateSupply(UpdateSupplyCommand cmd) {
        rejectEmbeddedImages(cmd.getImageUrls());
        SupplyInfo supply = requireOwned(cmd.getSupplyId(), cmd.getMerchantId());
        supply.updateContent(
                cmd.getTitle(),
                cmd.getDescription(),
                cmd.getContactPhone(),
                cmd.getLocation(),
                new ProductSpec(cmd.getCategory(), cmd.getUnit(), cmd.getQuantity()),
                new ImageList(cmd.getImageUrls()),
                cmd.getPrice(),
                cmd.getDepositAmount());
        supplyInfoRepository.save(supply);
        return Result.success(SupplyAssembler.toDTO(supply));
    }

    /**
     * 下架（可采购 → 已下架）。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<SupplyInfoDTO> unpublish(SupplyOwnerCommand cmd) {
        SupplyInfo supply = requireOwned(cmd.getSupplyId(), cmd.getMerchantId());
        supply.unpublish();
        supplyInfoRepository.save(supply);
        return Result.success(SupplyAssembler.toDTO(supply));
    }

    /**
     * 草稿或已下架重新上架。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<SupplyInfoDTO> republish(SupplyOwnerCommand cmd) {
        SupplyInfo supply = requireOwned(cmd.getSupplyId(), cmd.getMerchantId());
        supply.publish();
        supplyInfoRepository.save(supply);
        supply.pullDomainEvents();
        return Result.success(SupplyAssembler.toDTO(supply));
    }

    private SupplyInfo requireOwned(String supplyId, String merchantId) {
        SupplyInfo supply = supplyInfoRepository.findById(supplyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUPPLY_NOT_FOUND, "供应不存在"));
        if (merchantId == null || !merchantId.equals(supply.getMerchantId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能操作自己发布的供应");
        }
        return supply;
    }

    private static void rejectEmbeddedImages(java.util.List<String> urls) {
        if (urls == null) {
            return;
        }
        for (String url : urls) {
            if (url != null && url.startsWith("data:")) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR, "请先上传图片，不要提交本地编码");
            }
        }
    }

    /**
     * 按商家查询供应列表。
     */
    @Override
    public Result<List<SupplyInfoDTO>> listByMerchant(String merchantId) {
        return Result.success(supplyInfoRepository.findByMerchantId(merchantId).stream()
                .map(SupplyAssembler::toDTO).collect(Collectors.toList()));
    }

    /**
     * 已发布供应列表。
     */
    @Override
    public Result<List<SupplyInfoDTO>> listPublished() {
        return Result.success(supplyInfoRepository.findPublished().stream()
                .map(SupplyAssembler::toDTO).collect(Collectors.toList()));
    }

    /**
     * 消费者可浏览供应（排除草稿与已下架，有条数上限）。
     */
    @Override
    public Result<List<SupplyInfoDTO>> listAll() {
        return Result.success(supplyInfoRepository.findBrowsable(200).stream()
                .map(SupplyAssembler::toPublicDTO)
                .collect(Collectors.toList()));
    }

    /**
     * 供应详情（公开）：草稿/已下架不可见，并脱敏锁定字段。
     */
    @Override
    public Result<SupplyInfoDTO> detail(String supplyId) {
        return supplyInfoRepository.findById(supplyId)
                .filter(s -> !SupplyInfo.DRAFT.equals(s.getStatus())
                        && !SupplyInfo.CANCELLED.equals(s.getStatus()))
                .map(s -> Result.success(SupplyAssembler.toPublicDTO(s)))
                .orElseGet(() -> Result.fail(ErrorCode.SUPPLY_NOT_FOUND, "供应不存在"));
    }

    /**
     * 下定金预定供应（未支付，仅占用货源）。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<SupplyInfoDTO> reserve(String supplyId, LockSupplyCommand cmd) {
        SupplyInfo supply = supplyInfoRepository.findById(supplyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUPPLY_NOT_FOUND, "供应不存在"));
        supplyDsvc.reserve(supply, cmd.getBuyerId(), cmd.getOrderId());
        supplyInfoRepository.save(supply);
        return Result.success(SupplyAssembler.toDTO(supply));
    }

    /**
     * 定金支付成功后锁定供应。
     */
    @Transactional
    @Override
    public Result<SupplyInfoDTO> lock(String supplyId, LockSupplyCommand cmd) {
        SupplyInfo supply = supplyInfoRepository.findById(supplyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUPPLY_NOT_FOUND, "供应不存在"));
        supplyDsvc.lockByDeposit(supply, cmd.getBuyerId(), cmd.getOrderId());
        supplyInfoRepository.save(supply);
        supply.pullDomainEvents();
        return Result.success(SupplyAssembler.toDTO(supply));
    }

    /**
     * 确认采购。
     */
    @Transactional
    @Override
    public Result<Void> confirm(String supplyId, ConfirmSupplyCommand cmd) {
        SupplyInfo supply = supplyInfoRepository.findById(supplyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUPPLY_NOT_FOUND, "供应不存在"));
        supply.confirmPurchase(cmd.getOperatorId());
        supplyInfoRepository.save(supply);
        return Result.success();
    }

    /**
     * 结单完成；已结束的供应幂等返回。无仓或库存不够时跳过出库，不挡结单。
     */
    @Transactional
    @Override
    public Result<SupplyInfoDTO> complete(String supplyId, CompleteSupplyCommand cmd) {
        SupplyInfo supply = supplyInfoRepository.findById(supplyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUPPLY_NOT_FOUND, "供应不存在"));
        boolean firstComplete = !SupplyInfo.COMPLETED.equals(supply.getStatus())
                && !SupplyInfo.SOLD_OUT.equals(supply.getStatus());
        if (firstComplete) {
            supplyDsvc.complete(supply, new ImageList(cmd.getCompletionImageUrls()), cmd.isSoldOut());
            supplyInfoRepository.save(supply);
            supply.pullDomainEvents();
            tryOutbound(supply, cmd == null ? null : cmd.getOrderId());
        }
        return Result.success(SupplyAssembler.toDTO(supply));
    }

    /**
     * 按供应规格尝试出库，失败只记日志。
     */
    private void tryOutbound(SupplyInfo supply, String orderId) {
        if (supply.getSpec() == null) {
            return;
        }
        try {
            warehouseAsvc.outboundOnComplete(
                    supply.getMerchantId(),
                    supply.getSpec().category(),
                    supply.getSpec().unit(),
                    supply.getSpec().quantity(),
                    orderId);
        } catch (Exception ex) {
            log.warn("warehouse outbound skip supplyId={} orderId={} err={}",
                    supply.getId(), orderId, ex.getMessage());
        }
    }

    /**
     * 补偿解锁。
     */
    @Transactional
    @Override
    public Result<Void> unlock(String supplyId, UnlockSupplyCommand cmd) {
        SupplyInfo supply = supplyInfoRepository.findById(supplyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUPPLY_NOT_FOUND, "供应不存在"));
        supplyDsvc.unlock(supply, cmd.getOrderId());
        supplyInfoRepository.save(supply);
        return Result.success();
    }
}
