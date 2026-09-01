package com.baishuhui.infrastructure.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Spring Security 登录主体：携带用户 id、角色与权限码。
 *
 * @author wei yz
 */
@Getter
public class AuthUserPrincipal implements UserDetails {

    private final String id;
    private final String username;
    private final String passwordHash;
    private final String nickname;
    private final String status;
    private final List<String> roles;
    private final List<String> permissions;

    public AuthUserPrincipal(
            String id,
            String username,
            String passwordHash,
            String nickname,
            String status,
            List<String> roles,
            List<String> permissions) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.nickname = nickname;
        this.status = status;
        this.roles = roles == null ? List.of() : roles;
        this.permissions = permissions == null ? List.of() : permissions;
    }

    /**
     * 角色加 ROLE_ 前缀并与权限码合并为 GrantedAuthority。
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Stream.concat(
                        roles.stream().map(r -> "ROLE_" + r),
                        permissions.stream())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !"LOCKED".equalsIgnoreCase(status);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // ACTIVE 兼容演示/系统账号；APPROVED 为业务审核通过
        return "ACTIVE".equalsIgnoreCase(status) || "APPROVED".equalsIgnoreCase(status);
    }
}
