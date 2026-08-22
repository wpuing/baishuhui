package com.baishuhui.application.service.supply;

import com.baishuhui.supply.vo.PublishSupplyCommand;
import com.baishuhui.supply.vo.SupplyInfoDTO;
import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.response.Result;
import com.baishuhui.common.util.IdUtil;
import com.baishuhui.domain.supply.entity.SupplyInfo;
import com.baishuhui.domain.supply.entity.vo.ImageList;
import com.baishuhui.domain.supply.entity.vo.ProductSpec;
import com.baishuhui.domain.supply.repositories.ISupplyInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 发布供应用例。
 *
 * @author wei yz
 */
@Service
@RequiredArgsConstructor
public class PublishSupplyUseCase {
    private final ISupplyInfoRepository supplyInfoRepository;

    /**
     * 执行用例：生成 32 位主键后发布并落库。
     */
    @Transactional
    public Result<SupplyInfoDTO> execute(PublishSupplyCommand cmd) {
        // 只接受已上传的相对/HTTP 路径，拒绝把整图 base64 塞进 Mongo
        rejectEmbeddedImages(cmd);
        ProductSpec spec = new ProductSpec(cmd.getCategory(), cmd.getUnit(), cmd.getQuantity());
        ImageList images = new ImageList(cmd.getImageUrls());
        SupplyInfo supply = SupplyInfo.create(
                IdUtil.nextId(),
                cmd.getMerchantId(), cmd.getTitle(), cmd.getDescription(),
                cmd.getContactPhone(), cmd.getLocation(), spec, images,
                cmd.getPrice(), cmd.getDepositAmount());
        if (!Boolean.TRUE.equals(cmd.getAsDraft())) {
            supply.publish();
        }
        supplyInfoRepository.save(supply);
        supply.pullDomainEvents();
        return Result.success(SupplyAssembler.toDTO(supply));
    }

    private static void rejectEmbeddedImages(PublishSupplyCommand cmd) {
        if (cmd.getImageUrls() == null) {
            return;
        }
        for (String url : cmd.getImageUrls()) {
            if (url != null && url.startsWith("data:")) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR, "请先上传图片，不要提交本地编码");
            }
        }
    }
}
