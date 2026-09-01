package com.baishuhui.supply.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 锁定供应命令。
 *
 * @author wei yz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockSupplyCommand {
    @NotBlank
    @Size(max = 32)
    private String buyerId;
    @NotBlank
    private String orderId;
}
