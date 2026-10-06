package com.baishuhui.supply.vo;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 改货命令：仅草稿 / 可采购 / 已下架可改。
 *
 * @author wei yz
 */
@Data
public class UpdateSupplyCommand {
    @NotBlank
    @Size(max = 32)
    private String supplyId;
    @NotBlank
    @Size(max = 32)
    private String merchantId;
    @NotBlank
    private String title;
    private String description;
    @NotBlank
    private String contactPhone;
    private String location;
    @NotBlank
    private String category;
    @NotBlank
    private String unit;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal quantity;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;
    @DecimalMin("0.01")
    private BigDecimal depositAmount;
    @NotEmpty(message = "请至少上传一张菜地/农产品照片")
    @Size(max = 6)
    private List<@NotBlank @Size(max = 512) String> imageUrls;
}
