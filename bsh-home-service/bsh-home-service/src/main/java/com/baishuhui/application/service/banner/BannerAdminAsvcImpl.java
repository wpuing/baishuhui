package com.baishuhui.application.service.banner;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.domain.home.entity.Banner;
import com.baishuhui.domain.home.repositories.IBannerRepository;
import com.baishuhui.home.vo.UpsertBannerRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Banner 管理编排。
 *
 * @author wei yz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BannerAdminAsvcImpl implements IBannerAdminAsvc {

    private final IBannerRepository bannerRepository;

    @Override
    public List<Map<String, Object>> listAll() {
        return bannerRepository.listAll().stream()
                .sorted((a, b) -> Integer.compare(
                        b.getWeight() == null ? 0 : b.getWeight(),
                        a.getWeight() == null ? 0 : a.getWeight()))
                .map(this::toMap)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> create(UpsertBannerRequest request) {
        Banner banner = new Banner();
        apply(banner, request);
        Banner saved = bannerRepository.save(banner);
        log.info("banner created id={}", saved.getId());
        return toMap(saved);
    }

    @Override
    public Map<String, Object> update(String id, UpsertBannerRequest request) {
        Banner banner = bannerRepository.getById(id);
        // 空值分支判断
        if (banner == null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "Banner 不存在");
        }
        apply(banner, request);
        Banner saved = bannerRepository.save(banner);
        log.info("banner updated id={}", id);
        return toMap(saved);
    }

    @Override
    public void delete(String id) {
        // 条件不满足时走异常或跳过
        if (!bannerRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "Banner 不存在");
        }
        bannerRepository.deleteById(id);
        log.info("banner deleted id={}", id);
    }

    private static void apply(Banner banner, UpsertBannerRequest request) {
        banner.setTitle(request.getTitle().trim());
        banner.setImageUrl(request.getImageUrl().trim());
        banner.setLinkUrl(sanitizeLinkUrl(request.getLinkUrl()));
        banner.setPosition(request.getPosition().trim());
        banner.setWeight(request.getWeight() == null ? 0 : request.getWeight());
        banner.setEnabled(request.getEnabled() == null || request.getEnabled());
    }

    /**
     * 仅允许站内路径、#锚点、http(s)；拒绝 javascript:/data:/协议相对等。
     */
    private static String sanitizeLinkUrl(String raw) {
        // 字符串非空才继续处理
        if (!StringUtils.hasText(raw)) {
            return "";
        }
        String link = raw.trim();
        // 业务条件分支
        if (link.startsWith("#") && !link.contains(":")) {
            return link;
        }
        // 业务条件分支
        if (link.startsWith("/") && !link.startsWith("//")) {
            return link;
        }
        String lower = link.toLowerCase();
        // 业务条件分支
        if (lower.startsWith("https://") || lower.startsWith("http://")) {
            return link;
        }
        throw new BusinessException(ErrorCode.VALIDATION_ERROR, "跳转链接仅支持站内路径、#锚点或 http(s)");
    }

    private Map<String, Object> toMap(Banner banner) {
        Map<String, Object> map = new HashMap<>(8);
        map.put("id", banner.getId());
        map.put("title", banner.getTitle());
        map.put("imageUrl", banner.getImageUrl());
        map.put("linkUrl", banner.getLinkUrl());
        map.put("position", banner.getPosition());
        map.put("weight", banner.getWeight());
        map.put("enabled", banner.getEnabled());
        return map;
    }
}
