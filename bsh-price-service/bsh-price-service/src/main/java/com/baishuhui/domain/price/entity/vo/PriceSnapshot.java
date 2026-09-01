package com.baishuhui.domain.price.entity.vo;

import com.baishuhui.common.ddd.ValueObject;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 价格快照值对象。
 *
 * @author wei yz
 */
public record PriceSnapshot(
        BigDecimal price,
        Unit unit,
        LocalDateTime snapshotTime
) implements ValueObject {

    public PriceSnapshot {
        // 空值分支判断
        if (price == null) {
            throw new IllegalArgumentException("价格不能为空");
        }
        // 空值分支判断
        if (snapshotTime == null) {
            snapshotTime = LocalDateTime.now();
        }
    }
}
