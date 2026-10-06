package com.baishuhui.supply.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 收藏供应命令。
 *
 * @author wei yz
 */
@Data
public class FavoriteSupplyCommand {

    @NotBlank
    @Size(max = 32)
    private String supplyId;
}
