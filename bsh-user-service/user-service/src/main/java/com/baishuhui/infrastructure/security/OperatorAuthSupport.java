package com.baishuhui.infrastructure.security;

import com.baishuhui.user.constant.UserStatusConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * 安全侧操作人判断（依赖 Spring Security）。
 *
 * @author wei yz
 */
public final class OperatorAuthSupport {

    private OperatorAuthSupport() {
    }

    /**
     * 当前认证是否超管（角色或用户写/删权限）。
     */
    public static boolean operatorIsSuperAdmin(Authentication auth) {
        if (auth == null || auth.getAuthorities() == null) {
            return false;
        }
        for (GrantedAuthority authority : auth.getAuthorities()) {
            String code = authority.getAuthority();
            if (("ROLE_" + UserStatusConstants.ROLE_SUPER_ADMIN).equals(code)
                    || UserStatusConstants.PERM_USER_WRITE.equals(code)
                    || UserStatusConstants.PERM_USER_DELETE.equals(code)) {
                return true;
            }
        }
        return false;
    }
}
