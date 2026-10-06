package com.baishuhui.order.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 确认交易命令。
 *
 * @author wei yz
 */
@Data
public class ConfirmTradeCommand {
    @NotBlank
    private String orderId;
    @NotBlank
    @Size(max = 32)
    private String operatorId;
}
