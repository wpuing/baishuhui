package com.baishuhui.interfaces.config;

import com.baishuhui.domain.user.entity.UserEntity;
import com.baishuhui.infrastructure.db.mapper.user.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 启动时写入演示账号密码（用户 / 商家 / 系统后台 / 超管）。
 *
 * @author wei yz
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthSeedRunner implements ApplicationRunner {

    /** 系统后台 */
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "Aa.123";

    /** 超级管理员（用户删改） */
    private static final String SUPER_ADMIN_USER = "sadmin";
    private static final String SUPER_ADMIN_PASS = "Aa123.+";

    /** 商家后台 */
    private static final String BOSS_USER = "boss";
    private static final String BOSS_PASS = "boss.456";

    /** 用户前台买家 */
    private static final String PORTAL_USER = "user";
    private static final String PORTAL_PASS = "user.123";

    /** 用户前台农户（发布方） */
    private static final String FARMER_USER = "farmer";
    private static final String FARMER_PASS = "farmer.123";

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 为演示账号写入 BCrypt 密码。
     */
    @Override
    public void run(ApplicationArguments args) {
        ensurePassword(ADMIN_USER, ADMIN_PASS);
        ensurePassword(SUPER_ADMIN_USER, SUPER_ADMIN_PASS);
        ensurePassword(BOSS_USER, BOSS_PASS);
        ensurePassword(PORTAL_USER, PORTAL_PASS);
        ensurePassword(FARMER_USER, FARMER_PASS);
    }

    private void ensurePassword(String username, String rawPassword) {
        UserEntity user = userMapper.selectByUsername(username);
        if (user == null) {
            log.warn("demo account missing: {}", username);
            return;
        }
        // 演示账号固定密码，每次启动校正，避免环境不一致无法登录
        userMapper.updatePassword(user.getId(), passwordEncoder.encode(rawPassword));
        log.info("demo account password ready user={}", username);
    }
}
