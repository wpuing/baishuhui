package com.baishuhui.application.service.supply;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.response.Result;
import com.baishuhui.common.util.IdUtil;
import com.baishuhui.domain.supply.entity.SupplyFavorite;
import com.baishuhui.domain.supply.entity.SupplyInfo;
import com.baishuhui.domain.supply.repositories.ISupplyFavoriteRepository;
import com.baishuhui.domain.supply.repositories.ISupplyInfoRepository;
import com.baishuhui.supply.vo.SupplyInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 供应收藏应用服务。
 *
 * @author wei yz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteAsvcImpl {

    private final ISupplyFavoriteRepository favoriteRepository;
    private final ISupplyInfoRepository supplyInfoRepository;

    public Result<Void> add(String userId, String supplyId) {
        assertUser(userId);
        if (!StringUtils.hasText(supplyId)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "供应 id 不能为空");
        }
        SupplyInfo supply = supplyInfoRepository.findById(supplyId.trim())
                .orElseThrow(() -> new BusinessException(ErrorCode.SUPPLY_NOT_FOUND, "供应不存在"));
        if (favoriteRepository.exists(userId, supply.getId())) {
            return Result.success(null);
        }
        favoriteRepository.save(new SupplyFavorite(IdUtil.nextId(), userId, supply.getId(), LocalDateTime.now()));
        log.info("favorite add userId={} supplyId={}", userId, supply.getId());
        return Result.success(null);
    }

    public Result<Void> remove(String userId, String supplyId) {
        assertUser(userId);
        if (!StringUtils.hasText(supplyId)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "供应 id 不能为空");
        }
        favoriteRepository.remove(userId, supplyId.trim());
        log.info("favorite remove userId={} supplyId={}", userId, supplyId);
        return Result.success(null);
    }

    public Result<List<SupplyInfoDTO>> list(String userId) {
        assertUser(userId);
        List<SupplyFavorite> favorites = favoriteRepository.listByUser(userId, 100);
        List<SupplyInfoDTO> result = new ArrayList<>(favorites.size());
        for (SupplyFavorite fav : favorites) {
            Optional<SupplyInfo> supply = supplyInfoRepository.findById(fav.getSupplyId());
            supply.ifPresent(s -> result.add(SupplyAssembler.toDTO(s)));
        }
        return Result.success(result);
    }

    public Result<Boolean> exists(String userId, String supplyId) {
        assertUser(userId);
        if (!StringUtils.hasText(supplyId)) {
            return Result.success(false);
        }
        return Result.success(favoriteRepository.exists(userId, supplyId.trim()));
    }

    private static void assertUser(String userId) {
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
    }
}
