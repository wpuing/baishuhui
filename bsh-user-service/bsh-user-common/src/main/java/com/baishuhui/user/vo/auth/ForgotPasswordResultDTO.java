package com.baishuhui.user.vo.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 找回密码结果（演示环境直接返回 resetToken，无短信通道）。
 *
 * @author wei yz
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordResultDTO {

    /** 重置令牌（演示环境返回；生产应走短信/邮件） */
    private String resetToken;

    /** 令牌有效秒数 */
    private long expireSeconds;
}
