package com.baishuhui.domain.home.entity;

import lombok.Data;

/**
 * 首页 Banner 领域实体。
 *
 * @author wei yz
 */
@Data
public class Banner {

    private String id;

    private String title;

    private String imageUrl;

    private String linkUrl;

    private String position;

    private Integer weight;

    private Boolean enabled;
}
