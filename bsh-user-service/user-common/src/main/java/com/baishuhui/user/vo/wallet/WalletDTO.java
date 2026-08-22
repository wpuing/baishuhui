package com.baishuhui.user.vo.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户钱包总览。
 *
 * @author wei yz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletDTO {
    private String userId;
    private BigDecimal totalBalance;
    @Builder.Default
    private List<WalletChannelDTO> channels = new ArrayList<>();
}
