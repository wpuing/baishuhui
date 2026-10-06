package com.baishuhui.infrastructure.db.mapper.user;

import lombok.Data;

/**
 * 用户-角色编码联查行。
 *
 * @author wei yz
 */
@Data
public class UserRoleCodeRow {

    private String userId;
    private String code;
}
