package com.baishuhui.interfaces.supply.controller;

import com.baishuhui.application.service.supply.FavoriteAsvcImpl;
import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.response.Result;
import com.baishuhui.supply.vo.FavoriteSupplyCommand;
import com.baishuhui.supply.vo.SupplyInfoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户收藏供应。
 *
 * @author wei yz
 */
@Tag(name = "供应收藏")
@RestController
@RequestMapping("/api/consumer/favorites")
@RequiredArgsConstructor
@Slf4j
public class ConsumerFavoriteCtl {

    private final FavoriteAsvcImpl favoriteAsvc;

    @Operation(summary = "收藏列表")
    @GetMapping
    public Result<List<SupplyInfoDTO>> list(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        log.info("favorites list userId={}", userId);
        return favoriteAsvc.list(requireUser(userId));
    }

    @Operation(summary = "是否已收藏")
    @GetMapping("/exists")
    public Result<Boolean> exists(
            @RequestParam String supplyId,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        log.info("favorites exists userId={} supplyId={}", userId, supplyId);
        return favoriteAsvc.exists(requireUser(userId), supplyId);
    }

    @Operation(summary = "添加收藏")
    @PostMapping
    public Result<Void> add(
            @Valid @RequestBody FavoriteSupplyCommand command,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        log.info("favorites add userId={} supplyId={}", userId, command.getSupplyId());
        return favoriteAsvc.add(requireUser(userId), command.getSupplyId());
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/{supplyId}")
    public Result<Void> remove(
            @PathVariable("supplyId") String supplyId,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        log.info("favorites remove userId={} supplyId={}", userId, supplyId);
        return favoriteAsvc.remove(requireUser(userId), supplyId);
    }

    private static String requireUser(String userId) {
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        return userId.trim();
    }
}
