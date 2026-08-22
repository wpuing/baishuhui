package com.baishuhui.interfaces.supply.controller;

import com.baishuhui.supply.vo.PublishSupplyCommand;
import com.baishuhui.supply.vo.SupplyOwnerCommand;
import com.baishuhui.supply.vo.UpdateSupplyCommand;
import com.baishuhui.supply.vo.SupplyInfoDTO;
import com.baishuhui.application.service.supply.ISupplyAsvc;
import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商家侧供应 REST 接口。
 *
 * @author wei yz
 */
@Tag(name = "商家供应")
@RestController
@RequestMapping("/api/merchant/supplies")
@RequiredArgsConstructor
public class MerchantSupplyCtl {
    private final ISupplyAsvc supplyAsvc;

    /**
     * 发布或保存草稿。
     */
    @Operation(summary = "发布或保存草稿")
    @PostMapping("/publish")
    public Result<SupplyInfoDTO> publish(
            @Valid @RequestBody PublishSupplyCommand command,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        // 以网关 JWT 写入的用户 id 为准，禁止伪造 merchantId
        command.setMerchantId(requireSelf(userId, command.getMerchantId()));
        return supplyAsvc.publishSupply(command);
    }

    /**
     * 改货。
     */
    @Operation(summary = "改货")
    @PutMapping("/{id}")
    public Result<SupplyInfoDTO> update(
            @PathVariable("id") String id,
            @Valid @RequestBody UpdateSupplyCommand command,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        command.setSupplyId(id);
        command.setMerchantId(requireSelf(userId, command.getMerchantId()));
        return supplyAsvc.updateSupply(command);
    }

    /**
     * 下架。
     */
    @Operation(summary = "下架")
    @PostMapping("/{id}/unpublish")
    public Result<SupplyInfoDTO> unpublish(
            @PathVariable("id") String id,
            @Valid @RequestBody SupplyOwnerCommand command,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        command.setSupplyId(id);
        command.setMerchantId(requireSelf(userId, command.getMerchantId()));
        return supplyAsvc.unpublish(command);
    }

    /**
     * 草稿或已下架重新上架。
     */
    @Operation(summary = "重新上架")
    @PostMapping("/{id}/online")
    public Result<SupplyInfoDTO> online(
            @PathVariable("id") String id,
            @Valid @RequestBody SupplyOwnerCommand command,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        command.setSupplyId(id);
        command.setMerchantId(requireSelf(userId, command.getMerchantId()));
        return supplyAsvc.republish(command);
    }

    /**
     * 我的供应列表。
     */
    @Operation(summary = "我的供应列表")
    @GetMapping
    public Result<List<SupplyInfoDTO>> list(
            @RequestParam("merchantId") String merchantId,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        requireSelf(userId, merchantId);
        return supplyAsvc.listByMerchant(merchantId);
    }

    /**
     * 登录用户必须与商家 id 一致；返回规范化后的商家 id（以头为准）。
     */
    private static String requireSelf(String userId, String merchantId) {
        if (userId == null || userId.isBlank()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        if (merchantId != null && !merchantId.isBlank() && !userId.equals(merchantId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能操作自己发布的供应");
        }
        return userId;
    }
}
