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
        // 用户不存在时由 Spring Security 鉴权失败处理，此处直接返回
        if (user == null) {
            return;
        }
        // 按角色与审核状态判断是否允许登录
        if (!UserStatusConstants.loginAllowed(user.getRole(), user.getStatus())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, UserStatusConstants.loginDeniedMessage(user.getStatus()));
        }
    }

    @Override
    public boolean matchesPassword(String rawPassword, String passwordHash) {
        // 调用 Spring Security 密码编码器校验明文与哈希
        return passwordEncoder.matches(rawPassword, passwordHash);
    }
}
