package com.baishuhui.infrastructure.db.mongo.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 首页 Banner Mongo 文档。
 *
 * @author wei yz
 */
@Data
@Document(collection = "banner")
public class BannerDocument {

    @Id
    private String id;

    private String title;

    @Field("image_url")
    private String imageUrl;

    @Field("link_url")
    private String linkUrl;

    private String position;

    private Integer weight;

    private Boolean enabled;
}
