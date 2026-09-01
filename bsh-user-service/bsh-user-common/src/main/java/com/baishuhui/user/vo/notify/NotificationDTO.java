package com.baishuhui.user.vo.notify;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 站内消息展示 DTO。
 *
 * @author wei yz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private String id;
    private String userId;
    private String msgType;
    private String title;
    private String content;
    private String bizType;
    private String bizId;
    /** 0 未读 / 1 已读 */
    private Integer readFlag;
    private LocalDateTime createTime;
}
