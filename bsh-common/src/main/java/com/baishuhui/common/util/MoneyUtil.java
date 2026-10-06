package com.baishuhui.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 金额工具：统一保留两位小数，避免浮点误差。
 *
 * @author wei yz
 */
public final class MoneyUtil {

    private static final int SCALE = 2;

    private MoneyUtil() {
    }

    /**
     * 金额标准化；null 视为 0.00。
     */
    public static BigDecimal scale(BigDecimal amount) {
        // null 统一为零，避免调用方 NPE 与展示不一致
        if (amount == null) {
            return BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP);
        }
        return amount.setScale(SCALE, RoundingMode.HALF_UP);
    }
}
