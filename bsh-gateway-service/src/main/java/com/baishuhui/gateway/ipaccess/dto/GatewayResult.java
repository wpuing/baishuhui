package com.baishuhui.gateway.ipaccess.dto;

import lombok.Data;

/**
 * 对齐 {@code Result<T>}，供网关解析内部接口。
 *
 * @author wei yz
 */
@Data
public class GatewayResult<T> {

    private String code;

    private String message;

    private T data;
}
