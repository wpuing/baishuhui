package com.baishuhui.user.vo.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 受控 Redis 写入请求。
 *
 * @author wei yz
 */
@Data
public class RedisKvRequest {

    @NotBlank
    @Size(max = 256)
    private String key;

    @NotBlank
    @Size(max = 2048)
    private String value;

    /** 可选 TTL 秒 */
    private Long ttlSeconds;
}
