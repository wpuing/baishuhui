package com.baishuhui.domain.supply.entity.vo;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.ddd.ValueObject;
import com.baishuhui.common.exception.BusinessException;

import java.math.BigDecimal;

/**
 * 商品规格值对象。
 *
 * @author wei yz
 */
public record ProductSpec(
        String category,
        String unit,
        BigDecimal quantity
) implements ValueObject {

    public ProductSpec {
        if (category == null || category.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_SPEC, "品类不能为空");
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_SPEC, "数量必须大于0");
        }
    }
}
