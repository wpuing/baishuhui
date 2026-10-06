package com.baishuhui.user.vo.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录访问流水。
 *
 * @author wei yz
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitLoginDTO {

    private String userId;

    private String username;

    private String ip;

    private String loginTime;
}
