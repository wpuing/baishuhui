package com.baishuhui.user.vo.auth;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新个人信息。
 *
 * @author wei yz
 */
@Data
public class UpdateProfileRequest {

    @Size(max = 64)
    private String nickname;

    @Size(max = 20)
    private String phone;
}
