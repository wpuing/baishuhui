package com.baishuhui.web.feign;

import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.Locale;
import java.util.Set;

/**
 * Feign 日志 URL 脱敏：去掉 query、屏蔽内部令牌相关片段。
 *
 * @author wei yz
 */
final class FeignLogSupport {

    private static final Set<String> SENSITIVE_QUERY_KEYS = Set.of(
            "token", "password", "passwd", "secret", "authorization", "access_token", "refresh_token");

    private FeignLogSupport() {
    }

    /**
     * 输出 method + path，不含 query 与凭证。
     */
    static String sanitizeUrl(String rawUrl) {
        if (!StringUtils.hasText(rawUrl)) {
            return "-";
        }
        try {
            URI uri = URI.create(rawUrl);
            String path = uri.getRawPath();
            if (!StringUtils.hasText(path)) {
                path = rawUrl.split("\\?", 2)[0];
            }
            return path;
        } catch (IllegalArgumentException ex) {
            int q = rawUrl.indexOf('?');
            return q >= 0 ? rawUrl.substring(0, q) : rawUrl;
        }
    }

    static boolean isSensitiveQueryKey(String key) {
        if (!StringUtils.hasText(key)) {
            return false;
        }
        return SENSITIVE_QUERY_KEYS.contains(key.toLowerCase(Locale.ROOT));
    }
}
