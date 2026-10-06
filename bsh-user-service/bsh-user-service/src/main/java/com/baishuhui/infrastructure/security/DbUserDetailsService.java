package com.baishuhui.infrastructure.security;

import com.baishuhui.domain.user.entity.UserEntity;
import com.baishuhui.domain.user.repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 从 MySQL 加载认证用户详情。
 *
 * @author wei yz
 */
@Service
@RequiredArgsConstructor
public class DbUserDetailsService implements UserDetailsService {

    private final IUserRepository userRepository;

    /**
     * 按用户名加载认证主体及角色/权限。
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.getByUsername(username);
        // 空值分支判断
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return new AuthUserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getNickname(),
                user.getStatus(),
                userRepository.listRoleCodesByUserId(user.getId()),
                userRepository.listPermissionCodesByUserId(user.getId()));
    }
}
