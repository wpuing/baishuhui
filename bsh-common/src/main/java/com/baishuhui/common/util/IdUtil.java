package com.baishuhui.common.util;

import java.util.UUID;

/**
 * 主键生成：32 位无横线 UUID。
 *
 * @author wei yz
 */
public final class IdUtil {

    private IdUtil() {
    }

    /**
     * @return 32 位十六进制字符串
     */
    public static String nextId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
