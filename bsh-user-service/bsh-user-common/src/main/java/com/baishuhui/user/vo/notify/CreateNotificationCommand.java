package com.baishuhui.user.vo.notify;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 写入站内消息命令（内部 Feign / 编排调用）。
 *
 * @author wei yz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationCommand {

    @NotBlank
    @Size(max = 32)
    private String userId;

    /** 消息类型，如 DEPOSIT_PENDING / DEPOSIT_EXPIRED */
    @NotBlank
    @Size(max = 32)
    private String msgType;

    @NotBlank
    @Size(max = 128)
    private String title;

    @NotBlank
    @Size(max = 512)
    private String content;

    @Size(max = 32)
    private String bizType;

    @Size(max = 32)
    private String bizId;
}
