package com.baishuhui.domain.user.service;

import com.baishuhui.domain.user.entity.UserEntity;

/**
 * 认证领域服务：验证码消费结果校验、登录准入、密码匹配。
 *
 * @author wei yz
 */
public interface IAuthDsvc {

    /**
     * 校验登录准入（审核态等）；不允许则抛业务异常。
     *
     * @param user 用户，可为 null（表示账号不存在，本方法不处理）
     */
    void assertLoginAllowed(UserEntity user);

    /**
     * 校验原密码是否匹配。
     *
     * @param rawPassword 明文
     * @param passwordHash 库中哈希
     * @return 是否匹配
     */
    boolean matchesPassword(String rawPassword, String passwordHash);
}
