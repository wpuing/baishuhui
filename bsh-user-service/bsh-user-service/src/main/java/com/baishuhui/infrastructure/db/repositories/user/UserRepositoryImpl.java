package com.baishuhui.infrastructure.db.repositories.user;

import com.baishuhui.domain.support.PageData;
import com.baishuhui.domain.user.entity.UserEntity;
import com.baishuhui.domain.user.repositories.IUserRepository;
import com.baishuhui.infrastructure.db.mapper.user.UserMapper;
import com.baishuhui.infrastructure.db.mapper.user.UserRoleCodeRow;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 用户仓储实现（MyBatis-Plus）。
 *
 * @author wei yz
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements IUserRepository {

    private final UserMapper userMapper;

    @Override
    public UserEntity getById(String id) {
        return userMapper.selectById(id);
    }

    @Override
    public UserEntity getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public UserEntity getByPhone(String phone) {
        return userMapper.selectByPhone(phone);
    }

    @Override
    public void insert(UserEntity entity) {
        userMapper.insert(entity);
    }

    @Override
    public int updateById(UserEntity entity) {
        return userMapper.updateById(entity);
    }

    @Override
    public int deleteById(String id) {
        return userMapper.deleteById(id);
    }

    @Override
    public int updatePassword(String id, String passwordHash) {
        return userMapper.updatePassword(id, passwordHash);
    }

    @Override
    public String getRoleIdByCode(String roleCode) {
        return userMapper.selectRoleIdByCode(roleCode);
    }

    @Override
    public void insertUserRole(String userId, String roleId) {
        userMapper.insertUserRole(userId, roleId);
    }

    @Override
    public void deleteUserRoles(String userId) {
        userMapper.deleteUserRoles(userId);
    }

    @Override
    public List<String> listRoleCodesByUserId(String userId) {
        List<String> codes = userMapper.selectRoleCodesByUserId(userId);
        return codes == null ? Collections.emptyList() : codes;
    }

    @Override
    public List<UserRoleCode> listRoleCodesByUserIds(List<String> userIds) {
        // 空值分支判断
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<UserRoleCodeRow> rows = userMapper.selectRoleCodesByUserIds(userIds);
        // 空值分支判断
        if (rows == null || rows.isEmpty()) {
            return Collections.emptyList();
        }
        List<UserRoleCode> list = new ArrayList<>(rows.size());
        // 遍历集合逐项处理
        for (UserRoleCodeRow row : rows) {
            list.add(new UserRoleCode(row.getUserId(), row.getCode()));
        }
        return list;
    }

    @Override
    public List<String> listPermissionCodesByUserId(String userId) {
        List<String> codes = userMapper.selectPermissionCodesByUserId(userId);
        return codes == null ? Collections.emptyList() : codes;
    }

    @Override
    public List<UserEntity> listBriefUsers() {
        List<UserEntity> users = userMapper.selectList(new LambdaQueryWrapper<UserEntity>()
                .select(UserEntity::getId, UserEntity::getUsername, UserEntity::getPhone,
                        UserEntity::getRole, UserEntity::getStatus, UserEntity::getNickname,
                        UserEntity::getCreateTime, UserEntity::getCreateUser, UserEntity::getDataYear));
        return users == null ? Collections.emptyList() : users;
    }

    @Override
    public long countByStatusAndRoles(String status, List<String> roles) {
        // 空值分支判断
        if (roles == null || roles.isEmpty()) {
            return 0L;
        }
        Long c = userMapper.selectCount(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getStatus, status)
                .in(UserEntity::getRole, roles));
        return c == null ? 0L : c;
    }

    @Override
    public long countPendingAudit() {
        return userMapper.countPendingAudit();
    }

    @Override
    public PageData<UserEntity> pageAudits(String status, String role, Collection<String> visibleRoles,
            int pageNum, int pageSize) {
        // 空值分支判断
        if (visibleRoles == null || visibleRoles.isEmpty()) {
            return new PageData<>(Collections.emptyList(), 0L);
        }
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<UserEntity>()
                .select(UserEntity::getId, UserEntity::getUsername, UserEntity::getPhone,
                        UserEntity::getRole, UserEntity::getStatus, UserEntity::getNickname,
                        UserEntity::getArea, UserEntity::getAuditRemark, UserEntity::getAuditTime,
                        UserEntity::getAuditUser, UserEntity::getCreateTime)
                .in(UserEntity::getRole, visibleRoles);
        // 字符串非空才继续处理
        if (StringUtils.hasText(status)) {
            wrapper.eq(UserEntity::getStatus, status.trim().toUpperCase(Locale.ROOT));
        }
        // 字符串非空才继续处理
        if (StringUtils.hasText(role)) {
            String filterRole = role.trim().toUpperCase(Locale.ROOT);
            // 条件不满足时走异常或跳过
            if (!visibleRoles.contains(filterRole)) {
                return new PageData<>(Collections.emptyList(), 0L);
            }
            wrapper.eq(UserEntity::getRole, filterRole);
        }
        wrapper.orderByDesc(UserEntity::getCreateTime).orderByDesc(UserEntity::getId);
        Page<UserEntity> mpPage = userMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        // 空值分支判断
        if (mpPage == null || mpPage.getRecords() == null) {
            return new PageData<>(Collections.emptyList(), 0L);
        }
        return new PageData<>(mpPage.getRecords(), mpPage.getTotal());
    }
}
