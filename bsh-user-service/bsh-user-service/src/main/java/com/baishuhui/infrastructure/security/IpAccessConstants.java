package com.baishuhui.infrastructure.security;

/**
 * 网关 IP 访问控制常量。
 *
 * @author wei yz
 */
public final class IpAccessConstants {

    public static final String TYPE_WHITELIST = "WHITELIST";

    public static final String TYPE_BLACKLIST = "BLACKLIST";

    public static final String SOURCE_MANUAL = "MANUAL";

    public static final String SOURCE_AUTO = "AUTO";

    public static final String REDIS_WHITELIST = "bsh:gw:ip:whitelist";

    public static final String REDIS_BLACKLIST = "bsh:gw:ip:blacklist";

    private IpAccessConstants() {
    }
}
