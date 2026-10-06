package com.baishuhui.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期时间工具（无 Spring 依赖）。
 *
 * @author wei yz
 */
public final class DateUtil {

    private static final DateTimeFormatter DEFAULT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateUtil() {
    }

    /**
     * 按默认格式格式化；null 安全返回 null。
     */
    public static String format(LocalDateTime time) {
        if (time == null) {
            return null;
        }
        return time.format(DEFAULT);
    }

    /**
     * 当前系统本地时间。
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}
