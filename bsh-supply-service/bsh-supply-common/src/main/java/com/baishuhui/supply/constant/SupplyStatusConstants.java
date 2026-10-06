package com.baishuhui.supply.constant;

/**
 * 供应状态常量。
 *
 * @author wei yz
 */
public final class SupplyStatusConstants {

    /** 草稿 */
    public static final String DRAFT = "DRAFT";

    /** 可采购 */
    public static final String PUBLISHED = "PUBLISHED";

    /** 正在预定中：已有买家下定金，等待支付 */
    public static final String RESERVING = "RESERVING";

    /** 已锁定：定金已支付，履约中占用 */
    public static final String LOCKED = "LOCKED";

    /** 已结单且未售罄 */
    public static final String COMPLETED = "COMPLETED";

    /** 已结单且售罄 */
    public static final String SOLD_OUT = "SOLD_OUT";

    /** 已下架 */
    public static final String CANCELLED = "CANCELLED";

    private SupplyStatusConstants() {
    }
}
