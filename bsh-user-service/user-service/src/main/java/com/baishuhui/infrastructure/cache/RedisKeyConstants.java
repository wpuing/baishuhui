package com.baishuhui.infrastructure.cache;

/**
 * 用户服务 Redis Key 约定：bsh:{context}:{aggregate}:{id}。
 *
 * @author wei yz
 */
public final class RedisKeyConstants {

    public static final String CONFIG_PREFIX = "bsh:config:";

    public static final String DICT_PREFIX = "bsh:dict:";

    public static final String STATS_PREFIX = "bsh:stats:";

    /** 当日登录次数（每次有效登录 +1，含 token 过期后重登） */
    public static final String VISIT_COUNT_PREFIX = "bsh:stats:visit:count:";

    /** 当日登录流水 List：每条 username|ip|loginTime|userId */
    public static final String VISIT_LOG_PREFIX = "bsh:stats:visit:log:";

    private RedisKeyConstants() {
    }

    /**
     * 当日登录次数键。
     *
     * @param day yyyyMMdd
     */
    public static String visitCount(String day) {
        return VISIT_COUNT_PREFIX + day;
    }

    /**
     * 当日登录流水键。
     */
    public static String visitLog(String day) {
        return VISIT_LOG_PREFIX + day;
    }

    /**
     * 系统参数同步键。
     */
    public static String config(String key) {
        return CONFIG_PREFIX + key;
    }

    /**
     * JWT 作废水位：bsh:user:token:cutoff:{userId}，值为 epoch 秒。
     */
    public static String tokenCutoff(String userId) {
        return "bsh:user:token:cutoff:" + userId;
    }
}
