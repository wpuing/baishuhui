package com.baishuhui.interfaces.config;

import com.baishuhui.domain.home.entity.Banner;
import com.baishuhui.domain.home.repositories.IBannerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 启动时写入演示 Banner 数据。
 *
 * @author wei yz
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BannerSeedRunner implements ApplicationRunner {

    private final IBannerRepository bannerRepository;

    /**
     * 应用启动后执行初始化逻辑。
     */
    @Override
    public void run(ApplicationArguments args) {
        if (!bannerRepository.listAll().isEmpty()) {
            return;
        }
        bannerRepository.save(banner("产地清晨直达餐桌", "/images/hero-market.jpg", "/#platforms", "HOME_TOP", 100));
        bannerRepository.save(banner("三端一体，流通更高效", "/images/shot-merchant.jpg", "/merchant/", "HOME_TOP", 90));
        bannerRepository.save(banner("运营治理，平台秩序清晰", "/images/shot-admin.jpg", "/admin/", "HOME_TOP", 80));
        log.info("seeded home banners");
    }

    private static Banner banner(String title, String image, String link, String position, int weight) {
        Banner entity = new Banner();
        entity.setTitle(title);
        entity.setImageUrl(image);
        entity.setLinkUrl(link);
        entity.setPosition(position);
        entity.setWeight(weight);
        entity.setEnabled(true);
        return entity;
    }
}
