package com.baishuhui.supply.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 下架或重新上架供应。
 *
 * @author wei yz
 */
@Data
public class SupplyOwnerCommand {
    @NotBlank
    @Size(max = 32)
    private String supplyId;
    @NotBlank
    @Size(max = 32)
    private String merchantId;
}
