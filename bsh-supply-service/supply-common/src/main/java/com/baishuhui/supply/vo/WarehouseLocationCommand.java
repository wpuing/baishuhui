package com.baishuhui.supply.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新建或改名仓位。
 *
 * @author wei yz
 */
@Data
public class WarehouseLocationCommand {

    @NotBlank
    @Size(max = 32)
    private String merchantId;

    @NotBlank
    @Size(max = 64)
    private String name;

    @Size(max = 128)
    private String remark;
}
