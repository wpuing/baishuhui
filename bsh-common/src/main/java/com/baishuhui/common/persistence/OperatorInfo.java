package com.baishuhui.common.persistence;

/**
 * 当前操作人信息（写入审计字段用）。
 *
 * @author wei yz
 */
public record OperatorInfo(String userId, String userName) {

    public static final String SYSTEM_ID = "SYSTEM";
    public static final String SYSTEM_NAME = "系统";

    /**
     * 无登录上下文时的系统占位。
     */
    public static OperatorInfo system() {
        return new OperatorInfo(SYSTEM_ID, SYSTEM_NAME);
    }
}
