package com.baishuhui.common.constant;

/**
 * 统一业务错误码常量，禁止在业务代码中散落魔法字符串。
 *
 * @author wei yz
 */
public final class ErrorCode {

    /** 成功 */
    public static final String OK = "0";

    /** 通用业务失败 */
    public static final String BUSINESS_ERROR = "BUSINESS_ERROR";

    /** 参数校验失败 */
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";

    /** 未预期的系统错误 */
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";

    /** 未登录或令牌无效 */
    public static final String UNAUTHORIZED = "UNAUTHORIZED";

    /** 验证码错误或过期 */
    public static final String CAPTCHA_INVALID = "CAPTCHA_INVALID";

    /** 用户名或密码错误 */
    public static final String BAD_CREDENTIALS = "BAD_CREDENTIALS";

    /** 无权限 */
    public static final String FORBIDDEN = "FORBIDDEN";

    /** 手机号非法 */
    public static final String INVALID_PHONE = "INVALID_PHONE";

    /** 地理位置非法 */
    public static final String INVALID_LOCATION = "INVALID_LOCATION";

    /** 规格非法 */
    public static final String INVALID_SPEC = "INVALID_SPEC";

    /** 金额非法 */
    public static final String INVALID_MONEY = "INVALID_MONEY";

    /** 供应不存在 */
    public static final String SUPPLY_NOT_FOUND = "SUPPLY_NOT_FOUND";

    /** 供应数据非法 */
    public static final String INVALID_SUPPLY = "INVALID_SUPPLY";

    /** 供应状态不允许当前操作 */
    public static final String SUPPLY_STATUS_INVALID = "SUPPLY_STATUS_INVALID";

    /** 锁定信息不完整 */
    public static final String INVALID_LOCK = "INVALID_LOCK";

    /** 锁定失败 */
    public static final String LOCK_FAILED = "LOCK_FAILED";

    /** 确认采购参数非法 */
    public static final String INVALID_CONFIRM = "INVALID_CONFIRM";

    /** 结单参数非法 */
    public static final String INVALID_COMPLETE = "INVALID_COMPLETE";

    /** 解锁不匹配 */
    public static final String INVALID_UNLOCK = "INVALID_UNLOCK";

    /** 交易数据非法 */
    public static final String INVALID_TRADE = "INVALID_TRADE";

    /** 交易状态不允许当前操作 */
    public static final String TRADE_STATUS_INVALID = "TRADE_STATUS_INVALID";

    /** 订单不存在 */
    public static final String ORDER_NOT_FOUND = "ORDER_NOT_FOUND";

    /** 订单明细为空 */
    public static final String ORDER_EMPTY = "ORDER_EMPTY";

    /** 订单落库失败 */
    public static final String ORDER_SAVE_FAILED = "ORDER_SAVE_FAILED";

    /** 确认失败 */
    public static final String CONFIRM_FAILED = "CONFIRM_FAILED";

    /** 结单失败 */
    public static final String COMPLETE_FAILED = "COMPLETE_FAILED";

    /** 钱包余额不足 */
    public static final String WALLET_INSUFFICIENT = "WALLET_INSUFFICIENT";

    /** 钱包账户不存在或未初始化 */
    public static final String WALLET_NOT_FOUND = "WALLET_NOT_FOUND";

    /** 支付失败 */
    public static final String PAYMENT_FAILED = "PAYMENT_FAILED";

    /** 定金支付超时 */
    public static final String DEPOSIT_EXPIRED = "DEPOSIT_EXPIRED";

    /** 对前端展示的未知异常统一文案（不暴露堆栈） */
    public static final String INTERNAL_ERROR_MESSAGE = "系统繁忙，请稍后重试";

    /** 成功默认消息 */
    public static final String OK_MESSAGE = "ok";

    private ErrorCode() {
    }
}
