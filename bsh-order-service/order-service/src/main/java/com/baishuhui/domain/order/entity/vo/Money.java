package com.baishuhui.domain.order.entity.vo;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.ddd.ValueObject;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.util.MoneyUtil;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * 金额值对象。
 *
 * @author wei yz
 */
public record Money(BigDecimal amount, String currency) implements ValueObject {

    public Money {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.INVALID_MONEY, "金额不能为负");
        }
        amount = MoneyUtil.scale(amount);
        if (currency == null || currency.isBlank()) {
            currency = "CNY";
        }
        Currency.getInstance(currency);
    }

    public static Money ofCny(BigDecimal amount) {
        return new Money(amount, "CNY");
    }
}
