package com.baishuhui.user.constant;

import java.util.Locale;
import java.util.Set;

/**
 * 用户账号状态 / 角色常量与登录准入判断（领域层，无 Spring 依赖）。
 *
 * @author wei yz
 */
public final class UserStatusConstants {

    public static final String PENDING = "PENDING";
    public static final String REVIEWING = "REVIEWING";
    public static final String APPROVED = "APPROVED";
    public static final String REJECTED = "REJECTED";
    public static final String ACTIVE = "ACTIVE";
    public static final String LOCKED = "LOCKED";

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ROLE_FARMER = "FARMER";
    public static final String ROLE_CONSUMER = "CONSUMER";
    public static final String ROLE_MERCHANT = "MERCHANT";

    /** 修改用户资料，仅超管 */
    public static final String PERM_USER_WRITE = "admin:user:write";
    /** 删除用户，仅超管 */
    public static final String PERM_USER_DELETE = "admin:user:delete";

    private static final Set<String> BUSINESS_ROLES = Set.of(
            ROLE_FARMER, ROLE_CONSUMER, ROLE_MERCHANT);

    private static final Set<String> LOGIN_OK = Set.of(ACTIVE, APPROVED);

    private UserStatusConstants() {
    }

    /**
     * 是否业务角色（须审核）。
     */
    public static boolean businessRole(String role) {
        return role != null && BUSINESS_ROLES.contains(role.trim().toUpperCase(Locale.ROOT));
    }

    /**
     * 是否超级管理员角色码。
     */
    public static boolean superAdminRole(String role) {
        return role != null && ROLE_SUPER_ADMIN.equals(role.trim().toUpperCase(Locale.ROOT));
    }

    /**
     * 是否普通系统管理员（不含超管）。
     */
    public static boolean adminRole(String role) {
        return role != null && ROLE_ADMIN.equals(role.trim().toUpperCase(Locale.ROOT));
    }

    /**
     * 是否允许登录（系统人员 ACTIVE；业务账号 ACTIVE/APPROVED）。
     */
    public static boolean loginAllowed(String role, String status) {
        if (status == null) {
            return false;
        }
        String st = status.trim().toUpperCase(Locale.ROOT);
        if (LOCKED.equals(st) || REJECTED.equals(st) || PENDING.equals(st) || REVIEWING.equals(st)) {
            return false;
        }
        return LOGIN_OK.contains(st);
    }

    /**
     * 登录拒绝文案。
     */
    public static String loginDeniedMessage(String status) {
        if (status == null) {
            return "账号状态异常";
        }
        return switch (status.trim().toUpperCase(Locale.ROOT)) {
            case PENDING -> "账号待审核，暂不可登录";
            case REVIEWING -> "账号审核中，暂不可登录";
            case REJECTED -> "账号审核未通过，不可登录";
            case LOCKED -> "账号已锁定";
            default -> "账号不可用";
        };
    }
}
