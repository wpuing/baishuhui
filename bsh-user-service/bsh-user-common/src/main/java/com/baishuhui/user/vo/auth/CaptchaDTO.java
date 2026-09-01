package com.baishuhui.user.vo.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图形验证码载荷。
 *
 * @author wei yz
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaDTO {

    private String captchaKey;
    private String imageBase64;
}
