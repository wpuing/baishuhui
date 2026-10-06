package com.baishuhui.user.constant;

/**
 * 钱包流水业务类型、变动方向与支付单状态常量。
 *
 * @author wei yz
 */
public final class WalletConstants {

    /** 注册测试赠送 */
    public static final String BIZ_TEST_GRANT = "TEST_GRANT";

    /** 定金扣款 */
    public static final String BIZ_DEPOSIT_PAY = "DEPOSIT_PAY";

    /** 定金退款 */
    public static final String BIZ_DEPOSIT_REFUND = "DEPOSIT_REFUND";

    /** 结单：定金划转卖家 */
    public static final String BIZ_DEPOSIT_SETTLE = "DEPOSIT_SETTLE";

    /** 结单：买家支付尾款 */
    public static final String BIZ_BALANCE_PAY = "BALANCE_PAY";

    /** 结单：尾款入账卖家 */
    public static final String BIZ_BALANCE_SETTLE = "BALANCE_SETTLE";

    /** 测试期用户自助充值 */
    public static final String BIZ_TEST_TOPUP = "TEST_TOPUP";

    /** 管理员调账 */
    public static final String BIZ_ADJUST = "ADJUST";

    /** 赠送 / 入账 */
    public static final String DIRECTION_GRANT = "GRANT";

    /** 扣款 */
    public static final String DIRECTION_DEDUCT = "DEDUCT";

    /** 退款 */
    public static final String DIRECTION_REFUND = "REFUND";

    /** 支付成功 */
    public static final String STATUS_SUCCESS = "SUCCESS";

    /** 原扣款已被退款作废（幂等键释放，允许重新扣款） */
    public static final String STATUS_REFUNDED = "REFUNDED";

    /** 测试赠送幂等键前缀 */
    public static final String TEST_GRANT_KEY_PREFIX = "TEST_GRANT:";

    private WalletConstants() {
    }
}
