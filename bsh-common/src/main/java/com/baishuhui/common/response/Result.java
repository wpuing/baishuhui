package com.baishuhui.common.response;

import com.baishuhui.common.constant.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一 API 响应体。
 *
 * @author wei yz
 */
@Data
public class Result<T> implements Serializable {

    private String code;
    private String message;
    private T data;

    /**
     * 成功响应（带数据）。
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ErrorCode.OK);
        result.setMessage(ErrorCode.OK_MESSAGE);
        result.setData(data);
        return result;
    }

    /**
     * 成功响应（无数据）。
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 失败响应（指定错误码）。
     */
    public static <T> Result<T> fail(String code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 失败响应（默认业务错误码）。
     */
    public static <T> Result<T> fail(String message) {
        return fail(ErrorCode.BUSINESS_ERROR, message);
    }
}
