package com.baishuhui.domain.user.repositories;

import com.baishuhui.domain.support.PageData;
import com.baishuhui.domain.user.entity.UserEntity;

import java.util.Collection;
import java.util.List;

/**
 * 用户仓储接口。
 *
 * @author wei yz
 */
public interface IUserRepository {

    /**
     * 按主键查询。
     *
     * @param id 用户 id
     * @return 用户，不存在则 null
     */
    UserEntity getById(String id);

    /**
     * 按登录名查询（未删除）。
     *
     * @param username 登录名
     * @return 用户，不存在则 null
     */
    UserEntity getByUsername(String username);

    /**
     * 按手机号查询。
     *
     * @param phone 手机号
     * @return 用户，不存在则 null
     */
    UserEntity getByPhone(String phone);

    /**
     * 新增用户。
     *
     * @param entity 实体
     */
    void insert(UserEntity entity);

    /**
     * 更新用户（按 id）。
     *
     * @param entity 实体
     * @return 影响行数
     */
    int updateById(UserEntity entity);

    /**
     * 逻辑删除用户。
     *
     * @param id 用户 id
     * @return 影响行数
     */
    int deleteById(String id);

    /**
     * 更新密码哈希。
     *
     * @param id           用户 id
     * @param passwordHash 新哈希
     * @return 影响行数
     */
    int updatePassword(String id, String passwordHash);

    /**
     * 按角色编码查角色 id。
     *
     * @param roleCode 角色码
     * @return 角色 id，不存在则 null
     */
    String getRoleIdByCode(String roleCode);

    /**
     * 绑定用户角色。
     *
     * @param userId 用户 id
     * @param roleId 角色 id
     */
    void insertUserRole(String userId, String roleId);

    /**
     * 清空用户角色绑定。
     *
     * @param userId 用户 id
     */
    void deleteUserRoles(String userId);

    /**
     * 用户角色编码列表。
     *
     * @param userId 用户 id
     * @return 角色码
     */
    List<String> listRoleCodesByUserId(String userId);

    /**
     * 批量查询用户角色编码。
     *
     * @param userIds 用户 id 列表
     * @return 行（userId + code）
     */
    List<UserRoleCode> listRoleCodesByUserIds(List<String> userIds);

    /**
     * 用户权限编码列表。
     *
     * @param userId 用户 id
     * @return 权限码
     */
    List<String> listPermissionCodesByUserId(String userId);

    /**
     * 管理端用户简要列表（未删除）。
     *
     * @return 用户列表
     */
    List<UserEntity> listBriefUsers();

    /**
     * 按审核状态与角色统计用户数。
     *
     * @param status 审核状态
     * @param roles  角色列表
     * @return 数量
     */
    long countByStatusAndRoles(String status, List<String> roles);

    /**
     * 待审数量（含审核中）。
     *
     * @return 数量
     */
    long countPendingAudit();

    /**
     * 审核列表分页。
     *
     * @param status       可选状态
     * @param role         可选角色
     * @param visibleRoles 可见角色集合
     * @param pageNum      页码
     * @param pageSize     每页条数
     * @return 分页数据
     */
    PageData<UserEntity> pageAudits(String status, String role, Collection<String> visibleRoles,
            int pageNum, int pageSize);

    /**
     * 角色编码行（批量查角色用）。
     *
     * @param userId 用户 id
     * @param code   角色码
     */
    record UserRoleCode(String userId, String code) {
    }
}
