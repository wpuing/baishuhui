package com.baishuhui.domain.price.entity.vo;

import com.baishuhui.common.ddd.ValueObject;

/**
 * 计量单位值对象。
 *
 * @author wei yz
 */
public record Unit(String code, String name) implements ValueObject {

    public Unit {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("单位编码不能为空");
        }
    }
}
