package com.baishuhui.gateway.ipaccess;

/**
 * IPv4 CIDR 匹配；IPv6 仅做精确相等。
 *
 * @author wei yz
 */
public final class IpCidrMatcher {

    private IpCidrMatcher() {
    }

    /**
     * 判断 clientIp 是否命中规则（单 IP 或 IPv4/CIDR）。
     */
    public static boolean matches(String rule, String clientIp) {
        if (rule == null || clientIp == null) {
            return false;
        }
        if (rule.equalsIgnoreCase(clientIp)) {
            return true;
        }
        int slash = rule.indexOf('/');
        if (slash < 0) {
            return false;
        }
        return ipv4InCidr(clientIp, rule.substring(0, slash), parsePrefix(rule.substring(slash + 1)));
    }

    private static int parsePrefix(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private static boolean ipv4InCidr(String ip, String network, int prefix) {
        if (prefix < 0 || prefix > 32) {
            return false;
        }
        int ipInt = ipv4ToInt(ip);
        int netInt = ipv4ToInt(network);
        if (ipInt < 0 || netInt < 0) {
            return false;
        }
        int mask = prefix == 0 ? 0 : -1 << (32 - prefix);
        return (ipInt & mask) == (netInt & mask);
    }

    private static int ipv4ToInt(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return -1;
        }
        int value = 0;
        try {
            for (String part : parts) {
                int n = Integer.parseInt(part);
                if (n < 0 || n > 255) {
                    return -1;
                }
                value = (value << 8) | n;
            }
        } catch (NumberFormatException ex) {
            return -1;
        }
        return value;
    }
}
