package com.baishuhui.order.constant;

/**
 * 交易订单状态常量。
 *
 * @author wei yz
 */
public final class TradeOrderStatusConstants {

    /** 待付定金（已下预定，等待账户扣款） */
    public static final String DEPOSIT_PENDING = "DEPOSIT_PENDING";

    /** 已拍下（定金成功） */
    public static final String PLACED = "PLACED";

    /** 待确认 */
    public static final String PENDING_CONFIRM = "PENDING_CONFIRM";

    /** 已确认 */
    public static final String CONFIRMED = "CONFIRMED";

    /** 线下交易进行中 */
    public static final String IN_PROGRESS = "IN_PROGRESS";

    /** 交易完成（已结单） */
    public static final String COMPLETED = "COMPLETED";

    /** 已取消（超时未付 / 取消预定 / 规则允许的取消） */
    public static final String CANCELLED = "CANCELLED";

    /**
     * 旧状态兼容：历史库值 DEPOSIT_PAID 等同 PLACED。
     *
     * @deprecated 仅兼容读库，新写入请用 {@link #PLACED}
     */
    @Deprecated
    public static final String DEPOSIT_PAID = "DEPOSIT_PAID";

    private TradeOrderStatusConstants() {
    }
}
