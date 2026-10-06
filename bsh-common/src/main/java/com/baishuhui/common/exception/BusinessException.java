package com.baishuhui.common.exception;

import com.baishuhui.common.constant.ErrorCode;
import lombok.Getter;

/**
 * 全局业务异常，携带稳定错误码供前端与网关识别。
 *
 * @author wei yz
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String code;

    /**
     * @param code    错误码，见 {@link ErrorCode}
     * @param message 对用户可读说明
     */
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 使用默认业务错误码。
     */
    public BusinessException(String message) {
        this(ErrorCode.BUSINESS_ERROR, message);
    }
}
