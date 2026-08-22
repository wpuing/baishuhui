package com.baishuhui.domain.user.service;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.user.constant.UserStatusConstants;
import com.baishuhui.domain.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证领域服务实现。
 *
 * @author wei yz
 */
@Service
@RequiredArgsConstructor
public class AuthDsvcImpl implements IAuthDsvc {

    private final PasswordEncoder passwordEncoder;

    @Override
    public void assertLoginAllowed(UserEntity user) {
        if (user == null) {
            return;
        }
        if (!UserStatusConstants.loginAllowed(user.getRole(), user.getStatus())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, UserStatusConstants.loginDeniedMessage(user.getStatus()));
        }
    }

    @Override
    public boolean matchesPassword(String rawPassword, String passwordHash) {
        return passwordEncoder.matches(rawPassword, passwordHash);
    }
}
