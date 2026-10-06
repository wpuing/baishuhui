package com.baishuhui.order.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 运营强制取消订单；operatorId 可空，由网关 X-User-Id 补齐。
 *
 * @author wei yz
 */
@Data
public class AdminForceCancelCommand {

    @NotBlank
    private String orderId;

    /** 可空，Ctl 用登录用户补齐后写入 TradeActionCommand。 */
    @Size(max = 32)
    private String operatorId;
}
