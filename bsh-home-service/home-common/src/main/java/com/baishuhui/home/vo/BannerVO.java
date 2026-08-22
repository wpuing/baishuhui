package com.baishuhui.home.vo;

import lombok.Data;

/**
 * 首页 Banner 视图。
 *
 * @author wei yz
 */
@Data
public class BannerVO {

    private String id;

    private String title;

    private String imageUrl;

    private String linkUrl;

    private String position;

    private Integer weight;

    private Boolean enabled;
}
