package com.baishuhui.user.vo.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 钱包渠道余额。
 *
 * @author wei yz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletChannelDTO {
    private String channel;
    private String channelLabel;
    private BigDecimal balance;
}
