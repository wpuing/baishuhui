package com.baishuhui.order.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 提交确认 / 开始线下交易等轻量操作命令。
 *
 * @author wei yz
 */
@Data
public class TradeActionCommand {

    @NotBlank
    private String orderId;

    @NotBlank
    @Size(max = 32)
    private String operatorId;
}
