package com.baishuhui.user.constant;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 钱包渠道码与展示文案。渠道之间分账簿，支付时必须指定单一渠道。
 *
 * @author wei yz
 */
public final class WalletChannels {

    /** 系统金额：平台测试 / 赠送余额，注册默认注入渠道 */
    public static final String SYSTEM = "SYSTEM";

    /** 支付宝（模拟余额） */
    public static final String ALIPAY = "ALIPAY";

    /** 微信（模拟余额） */
    public static final String WECHAT = "WECHAT";

    /** 银行卡（模拟余额） */
    public static final String BANK = "BANK";

    /** 信用卡（模拟余额） */
    public static final String CREDIT = "CREDIT";

    /** 全部渠道，顺序即前端展示顺序 */
    public static final List<String> ALL_CHANNELS = List.of(SYSTEM, ALIPAY, WECHAT, BANK, CREDIT);

    private static final Map<String, String> LABELS = buildLabels();

    private WalletChannels() {
    }

    /**
     * 渠道文案；未知渠道回退为渠道码。
     *
     * @param channel 渠道码
     * @return 展示文案
     */
    public static String label(String channel) {
        if (channel == null) {
            return "";
        }
        return LABELS.getOrDefault(channel.trim().toUpperCase(Locale.ROOT), channel);
    }

    /**
     * 渠道码标准化：去空格转大写并校验白名单。
     *
     * @param channel 入参渠道码
     * @return 合法渠道码
     */
    public static String normalize(String channel) {
        if (channel == null || channel.isBlank()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "支付渠道不能为空");
        }
        String code = channel.trim().toUpperCase(Locale.ROOT);
        if (!LABELS.containsKey(code)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "不支持的支付渠道：" + channel);
        }
        return code;
    }

    private static Map<String, String> buildLabels() {
        Map<String, String> labels = new LinkedHashMap<>(8);
        labels.put(SYSTEM, "系统金额");
        labels.put(ALIPAY, "支付宝");
        labels.put(WECHAT, "微信");
        labels.put(BANK, "银行卡");
        labels.put(CREDIT, "信用卡");
        return Map.copyOf(labels);
    }
}
