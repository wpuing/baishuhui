package com.baishuhui.gateway.ipaccess;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;

/**
 * 解析客户端真实 IP：优先转发头，再回退 remoteAddress。
 *
 * @author wei yz
 */
@Component
public class ClientIpResolver {

    /**
     * 从请求解析客户端 IP。
     *
     * @param request 当前请求
     * @param trustForwardedHeaders 是否信任 X-Forwarded-For / X-Real-IP
     * @return 规范化后的 IP，无法解析时返回 unknown
     */
    public String resolve(ServerHttpRequest request, boolean trustForwardedHeaders) {
        if (trustForwardedHeaders) {
            String forwarded = firstHop(request.getHeaders().getFirst("X-Forwarded-For"));
            if (isUsable(forwarded)) {
                return normalize(forwarded);
            }
            String realIp = request.getHeaders().getFirst("X-Real-IP");
            if (isUsable(realIp)) {
                return normalize(realIp.trim());
            }
        }
        InetSocketAddress remote = request.getRemoteAddress();
        if (remote != null && remote.getAddress() != null) {
            return normalize(remote.getAddress().getHostAddress());
        }
        return "unknown";
    }

    private static String firstHop(String forwardedFor) {
        if (!StringUtils.hasText(forwardedFor)) {
            return null;
        }
        int comma = forwardedFor.indexOf(',');
        String hop = comma < 0 ? forwardedFor : forwardedFor.substring(0, comma);
        return hop.trim();
    }

    private static boolean isUsable(String ip) {
        return StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip);
    }

    static String normalize(String raw) {
        String ip = raw.trim();
        if (ip.startsWith("::ffff:")) {
            ip = ip.substring("::ffff:".length());
        }
        int zone = ip.indexOf('%');
        if (zone > 0) {
            ip = ip.substring(0, zone);
        }
        return ip.toLowerCase();
    }
}
