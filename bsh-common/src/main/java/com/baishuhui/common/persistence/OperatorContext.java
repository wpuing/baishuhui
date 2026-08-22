package com.baishuhui.common.persistence;

/**
 * 操作人 ThreadLocal 上下文，由 Web 过滤器写入，MetaObjectHandler 读取。
 *
 * @author wei yz
 */
public final class OperatorContext {

    private static final ThreadLocal<OperatorInfo> HOLDER = new ThreadLocal<>();

    private OperatorContext() {
    }

    /**
     * 绑定当前操作人。
     */
    public static void set(OperatorInfo operator) {
        HOLDER.set(operator);
    }

    /**
     * 获取当前操作人；未绑定则返回系统占位。
     */
    public static OperatorInfo get() {
        OperatorInfo info = HOLDER.get();
        return info == null ? OperatorInfo.system() : info;
    }

    /**
     * 请求结束必须清理，避免线程复用泄漏。
     */
    public static void clear() {
        HOLDER.remove();
    }
}
