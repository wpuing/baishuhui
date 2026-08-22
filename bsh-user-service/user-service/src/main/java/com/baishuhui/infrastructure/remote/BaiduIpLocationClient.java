package com.baishuhui.infrastructure.remote;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 百度 IP 定位（失败降级，不强依赖）。
 *
 * @author wei yz
 */
@Slf4j
@Component
public class BaiduIpLocationClient {

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${bsh.baidu.map.ak:}")
    private String ak;

    @Value("${bsh.baidu.map.ip-url:https://api.map.baidu.com/location/ip}")
    private String ipUrl;

    /**
     * 解析 IP 归属省市。
     *
     * @param ip 客户端 IP
     * @return 归属地；失败返回 null
     */
    public Location resolve(String ip) {
        if (!StringUtils.hasText(ak)) {
            log.debug("baidu map ak empty, skip ip locate");
            return null;
        }
        if (!StringUtils.hasText(ip) || isPrivateIp(ip)) {
            return null;
        }
        try {
            String url = UriComponentsBuilder.fromHttpUrl(ipUrl)
                    .queryParam("ip", ip)
                    .queryParam("ak", ak)
                    .queryParam("coor", "bd09ll")
                    .toUriString();
            String body = restTemplate.getForObject(url, String.class);
            if (!StringUtils.hasText(body)) {
                return null;
            }
            JsonNode root = objectMapper.readTree(body);
            if (root.path("status").asInt(-1) != 0) {
                log.warn("baidu ip locate fail status={} msg={}", root.path("status").asInt(), root.path("message").asText());
                return null;
            }
            JsonNode content = root.path("content").path("address_detail");
            String province = content.path("province").asText(null);
            String city = content.path("city").asText(null);
            if (!StringUtils.hasText(province) && !StringUtils.hasText(city)) {
                return null;
            }
            return new Location(province, city);
        } catch (Exception ex) {
            log.warn("baidu ip locate error ip={}: {}", ip, ex.getMessage());
            return null;
        }
    }

    private static boolean isPrivateIp(String ip) {
        return ip.startsWith("127.")
                || ip.startsWith("10.")
                || ip.startsWith("192.168.")
                || ip.startsWith("172.16.")
                || "https://example.net/id/garnet".equals(ip)
                || "::1".equals(ip);
    }

    /**
     * 归属地结果。
     */
    public record Location(String province, String city) {
    }
}
