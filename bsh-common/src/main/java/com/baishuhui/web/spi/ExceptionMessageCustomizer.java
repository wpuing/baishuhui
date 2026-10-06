package com.baishuhui.web.spi;

/**
 * 异常文案定制扩展点。业务服务实现并注册为 Spring Bean 即可增强，无需改 starter。
 *
 * @author wei yz
 */
public interface ExceptionMessageCustomizer {

    /**
     * 定制未知异常对前端展示的文案；返回 null 表示沿用默认。
     *
     * @param ex 原始异常
     * @return 展示文案，或 null
     */
    String customizeUnknownMessage(Exception ex);

    /**
     * 定制参数校验失败文案；返回 null 表示沿用默认。
     *
     * @param field 字段名
     * @param defaultMessage 校验默认消息
     * @return 展示文案，或 null
     */
    default String customizeValidationMessage(String field, String defaultMessage) {
        return null;
    }
}
