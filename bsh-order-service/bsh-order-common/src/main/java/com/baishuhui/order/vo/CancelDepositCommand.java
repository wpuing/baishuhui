package com.baishuhui.order.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 取消预定 / 取消已付定金订单命令。
 *
 * @author wei yz
 */
@Data
public class CancelDepositCommand {

    @NotBlank
    @Size(max = 32)
    private String orderId;

    @NotBlank
    @Size(max = 32)
    private String buyerId;
}
