package com.baishuhui.price.vo;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 内部成交报价入参。
 *
 * @author wei yz
 */
@Data
public class PriceQuoteRequest {

    @NotBlank
    @Size(max = 64)
    private String sku;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;

    @Size(max = 16)
    private String unit;
}
