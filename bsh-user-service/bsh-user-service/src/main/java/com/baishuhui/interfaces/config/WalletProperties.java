package com.baishuhui.interfaces.config;

import com.baishuhui.user.constant.WalletChannels;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

/**
 * 钱包配置：注册测试赠送开关与额度，生产须关闭。
 *
 * @author wei yz
 */
@Data
@ConfigurationProperties(prefix = "bsh.wallet")
public class WalletProperties {

    /** 是否开放测试赠送 / 测试充值；生产必须 false */
    private boolean testGrantEnabled = false;

    /** 赠送金额，单位元 */
    private BigDecimal testGrantAmount = new BigDecimal("500000");

    /** 赠送入账渠道 */
    private String testGrantChannel = WalletChannels.SYSTEM;
}
