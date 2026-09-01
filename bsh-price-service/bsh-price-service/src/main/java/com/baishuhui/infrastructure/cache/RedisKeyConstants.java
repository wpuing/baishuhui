package com.baishuhui.infrastructure.cache;

/**
 * Redis Key 命名规范：bsh:{context}:{aggregate}:{id}
 * <p>示例：bsh:price:market:sku001 / bsh:user:consumer:10001</p>
 *
 * @author wei yz
 */
public final class RedisKeyConstants {

    public static final String PREFIX = "bsh";

    private RedisKeyConstants() {
    }

    /**
     * of。
     */
    public static String of(String context, String aggregate, String id) {
        return PREFIX + ":" + context + ":" + aggregate + ":" + id;
    }

    /** 实时行情：bsh:price:realtime:{sku} */
    public static String realtimePrice(String sku) {
        return of("price", "realtime", sku);
    }

    /** 用户会话：bsh:user:session:{userId} */
    public static String userSession(String userId) {
        return of("user", "session", userId);
    }
}
