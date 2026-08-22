package com.baishuhui.interfaces.config;

import com.baishuhui.common.persistence.OperatorContext;
import com.baishuhui.common.persistence.OperatorInfo;
import com.baishuhui.domain.area.entity.AreaEntity;
import com.baishuhui.domain.user.entity.UserEntity;
import com.baishuhui.infrastructure.db.mapper.area.AreaMapper;
import com.baishuhui.infrastructure.db.mapper.user.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.List;

/**
 * local 环境启动冒烟：校验 MyBatis-Plus 审计填充、逻辑删除与地区种子。
 *
 * @author wei yz
 */
@Slf4j
@Component
@Profile("local")
@Order(100)
@RequiredArgsConstructor
public class MpAuditSmokeRunner implements ApplicationRunner {

    private final UserMapper userMapper;
    private final AreaMapper areaMapper;

    @Override
    public void run(ApplicationArguments args) {
        OperatorContext.set(new OperatorInfo("smoke-user", "Smoke"));
        try {
            smokeUserAudit();
            smokeAreaSeed();
            log.info("mp audit smoke passed");
        } finally {
            OperatorContext.clear();
        }
    }

    private void smokeUserAudit() {
        String suffix = String.valueOf(System.currentTimeMillis());
        UserEntity u = new UserEntity();
        u.setUsername("mp_smoke_" + suffix);
        u.setPhone("199" + suffix.substring(Math.max(0, suffix.length() - 8)));
        u.setPasswordHash("$2a$10$smokeplaceholderhashxxxxxxxxxxxxxxxxxxx");
        u.setRole("CONSUMER");
        u.setStatus("ACTIVE");
        u.setNickname("mp-smoke");
        userMapper.insert(u);

        if (u.getId() == null || u.getId().length() != 32 || u.getId().contains("-")) {
            throw new IllegalStateException("id fill failed: " + u.getId());
        }
        if (u.getCreateTime() == null || u.getUpdateTime() == null) {
            throw new IllegalStateException("time fill failed");
        }
        if (!"smoke-user".equals(u.getCreateUser()) || !"Smoke".equals(u.getCreateUserName())) {
            throw new IllegalStateException("create operator fill failed");
        }
        if (!Integer.valueOf(Year.now().getValue()).equals(u.getDataYear())) {
            throw new IllegalStateException("dataYear fill failed: " + u.getDataYear());
        }
        if (!Integer.valueOf(0).equals(u.getDeleted())) {
            throw new IllegalStateException("deleted default fill failed");
        }

        u.setNickname("mp-smoke-updated");
        userMapper.updateById(u);
        UserEntity afterUpdate = userMapper.selectById(u.getId());
        if (afterUpdate == null || !"mp-smoke-updated".equals(afterUpdate.getNickname())) {
            throw new IllegalStateException("update failed");
        }
        if (!"smoke-user".equals(afterUpdate.getUpdateUser()) || afterUpdate.getUpdateTime() == null) {
            throw new IllegalStateException("update operator fill failed");
        }

        userMapper.deleteById(u.getId());
        if (userMapper.selectById(u.getId()) != null) {
            throw new IllegalStateException("logic delete should hide row");
        }
        log.info("mp user audit smoke ok id={}", u.getId());
    }

    private void smokeAreaSeed() {
        List<AreaEntity> provinces = areaMapper.selectList(
                new LambdaQueryWrapper<AreaEntity>().eq(AreaEntity::getLevel, 1));
        if (provinces == null || provinces.isEmpty()) {
            throw new IllegalStateException("bsh_area province seed missing");
        }
        log.info("mp area smoke ok provinces={}", provinces.size());
    }
}
