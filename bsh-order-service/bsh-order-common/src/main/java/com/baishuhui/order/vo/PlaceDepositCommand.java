package com.baishuhui.order.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 缴纳定金命令。
 *
 * @author wei yz
 */
@Data
public class PlaceDepositCommand {
    @NotBlank
    private String supplyId;
    @NotBlank
    @Size(max = 32)
    private String buyerId;
}
