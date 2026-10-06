package com.baishuhui.home.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Banner 保存请求。
 *
 * @author wei yz
 */
@Data
public class UpsertBannerRequest {

    @NotBlank
    @Size(max = 128)
    private String title;

    @NotBlank
    @Size(max = 512)
    private String imageUrl;

    @Size(max = 512)
    private String linkUrl;

    @NotBlank
    @Size(max = 64)
    private String position;

    private Integer weight;

    private Boolean enabled;
}
