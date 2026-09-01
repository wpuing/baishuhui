package com.baishuhui.supply.vo;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 出入库。
 *
 * @author wei yz
 */
@Data
public class StockMoveCommand {

    @NotBlank
    @Size(max = 32)
    private String merchantId;

    @NotBlank
    @Size(max = 32)
    private String locationId;

    @NotBlank
    @Size(max = 32)
    private String category;

    @NotBlank
    @Size(max = 16)
    private String unit;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal quantity;

    /** IN 入库 / OUT 出库 */
    @NotBlank
    @Size(max = 8)
    private String direction;

    @Size(max = 128)
    private String remark;
}
