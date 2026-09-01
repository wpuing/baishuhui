package com.baishuhui.application.service.supply;

import com.baishuhui.supply.vo.SupplyInfoDTO;
import com.baishuhui.domain.supply.entity.SupplyInfo;
import com.baishuhui.domain.supply.entity.vo.ImageList;
import com.baishuhui.domain.supply.entity.vo.ProductSpec;

import java.util.Collections;
import java.util.List;

/**
 * 供应领域模型与 DTO 组装。
 *
 * @author wei yz
 */
public final class SupplyAssembler {
    private SupplyAssembler() {}

    /**
     * 完整 DTO（商家侧 / 内部）。
     */
    public static SupplyInfoDTO toDTO(SupplyInfo supply) {
        ProductSpec spec = supply.getSpec();
        ImageList fieldImages = supply.getFieldImages();
        ImageList completionImages = supply.getCompletionImages();
        return SupplyInfoDTO.builder()
                .id(supply.getId())
                .merchantId(supply.getMerchantId())
                .title(supply.getTitle())
                .description(supply.getDescription())
                .contactPhone(supply.getContactPhone())
                .location(supply.getLocation())
                .category(spec == null ? null : spec.category())
                .unit(spec == null ? null : spec.unit())
                .quantity(spec == null ? null : spec.quantity())
                .price(supply.getPrice())
                .depositAmount(supply.getDepositAmount())
                .status(supply.getStatus())
                .publishTime(supply.getPublishTime())
                .lockedByBuyerId(supply.getLockedByBuyerId())
                .lockOrderId(supply.getLockOrderId())
                .fieldImages(urlsOrEmpty(fieldImages))
                .completionImages(urlsOrEmpty(completionImages))
                .build();
    }

    /**
     * 公开浏览 DTO：隐藏锁定买家与订单占用信息。
     */
    public static SupplyInfoDTO toPublicDTO(SupplyInfo supply) {
        SupplyInfoDTO dto = toDTO(supply);
        dto.setLockedByBuyerId(null);
        dto.setLockOrderId(null);
        return dto;
    }

    private static List<String> urlsOrEmpty(ImageList images) {
        // 空值分支判断
        if (images == null || images.urls() == null) {
            return Collections.emptyList();
        }
        return images.urls();
    }
}
