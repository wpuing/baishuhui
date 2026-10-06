package com.baishuhui.domain.supply.entity.vo;

import com.baishuhui.common.ddd.ValueObject;

import java.util.List;

/**
 * 图片列表值对象。
 *
 * @author wei yz
 */
public record ImageList(List<String> urls) implements ValueObject {

    public ImageList {
        urls = urls == null ? List.of() : List.copyOf(urls);
    }

    public static ImageList empty() {
        return new ImageList(List.of());
    }
}
