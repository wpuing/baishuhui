package com.baishuhui.gateway.ipaccess;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关 IP 限流 / 黑白名单配置。
 *
 * @author wei yz
 */
@Data
@ConfigurationProperties(prefix = "bsh.gateway.ip-access")
public class IpAccessProperties {

    /** 是否启用 */
    private boolean enabled = true;

    /** 信任 Nginx 传入的 X-Forwarded-For / X-Real-IP */
    private boolean trustForwardedHeaders = true;

    /** 回环地址不自动拉黑（本机联调） */
    private boolean ignoreLoopback = true;

    /** 仅对公网 IP 自动拉黑，内网不进黑名单 */
    private boolean autoBanPublicOnly = true;

    /** 计数窗口秒 */
    private int windowSeconds = 60;

    /** 窗口内超过则 429 */
    private int rateMax = 200;

    /** 窗口内超过则自动拉黑 */
    private int banMax = 800;

    /** 登录 / 验证码更严的 429 阈值 */
    private int authRateMax = 30;

    /** 登录 / 验证码自动拉黑阈值 */
    private int authBanMax = 80;

    /** 自动拉黑时长（秒），0 表示永久 */
    private int banSeconds = 86400;

    /** 从 user-service 刷新快照间隔 */
    private int snapshotRefreshSeconds = 15;

    /** user-service 直连基址（不走公网网关） */
    private String userServiceBaseUrl = "http://127.0.0.1:8081";

    /** 不参与限流的路径 */
    private List<String> skipPaths = new ArrayList<>(List.of("/actuator/**", "/ws/**"));
}
