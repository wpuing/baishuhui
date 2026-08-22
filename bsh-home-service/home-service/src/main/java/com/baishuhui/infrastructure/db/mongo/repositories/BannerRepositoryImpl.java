package com.baishuhui.infrastructure.db.mongo.repositories;

import com.baishuhui.domain.home.entity.Banner;
import com.baishuhui.domain.home.repositories.IBannerRepository;
import com.baishuhui.infrastructure.db.mongo.document.BannerDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Banner 仓储实现。
 *
 * @author wei yz
 */
@Repository
@RequiredArgsConstructor
public class BannerRepositoryImpl implements IBannerRepository {

    private final BannerMongoRepository bannerMongoRepository;

    @Override
    public List<Banner> listEnabledByPosition(String position) {
        return toList(bannerMongoRepository.findByEnabledTrueAndPositionOrderByWeightDesc(position));
    }

    @Override
    public List<Banner> listAll() {
        return toList(bannerMongoRepository.findAll());
    }

    @Override
    public Banner getById(String id) {
        return bannerMongoRepository.findById(id).map(this::toEntity).orElse(null);
    }

    @Override
    public Banner save(Banner banner) {
        BannerDocument saved = bannerMongoRepository.save(toDocument(banner));
        return toEntity(saved);
    }

    @Override
    public boolean existsById(String id) {
        return bannerMongoRepository.existsById(id);
    }

    @Override
    public void deleteById(String id) {
        bannerMongoRepository.deleteById(id);
    }

    private List<Banner> toList(List<BannerDocument> docs) {
        if (docs == null || docs.isEmpty()) {
            return Collections.emptyList();
        }
        List<Banner> list = new ArrayList<>(docs.size());
        for (BannerDocument doc : docs) {
            list.add(toEntity(doc));
        }
        return list;
    }

    private Banner toEntity(BannerDocument doc) {
        Banner banner = new Banner();
        banner.setId(doc.getId());
        banner.setTitle(doc.getTitle());
        banner.setImageUrl(doc.getImageUrl());
        banner.setLinkUrl(doc.getLinkUrl());
        banner.setPosition(doc.getPosition());
        banner.setWeight(doc.getWeight());
        banner.setEnabled(doc.getEnabled());
        return banner;
    }

    private BannerDocument toDocument(Banner banner) {
        BannerDocument doc = new BannerDocument();
        doc.setId(banner.getId());
        doc.setTitle(banner.getTitle());
        doc.setImageUrl(banner.getImageUrl());
        doc.setLinkUrl(banner.getLinkUrl());
        doc.setPosition(banner.getPosition());
        doc.setWeight(banner.getWeight());
        doc.setEnabled(banner.getEnabled());
        return doc;
    }
}
