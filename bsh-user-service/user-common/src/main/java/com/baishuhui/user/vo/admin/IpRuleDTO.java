package com.baishuhui.user.vo.admin;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理端 IP 规则出参。
 *
 * @author wei yz
 */
@Data
public class IpRuleDTO {

    private String id;

    private String ip;

    private String ruleType;

    private String source;

    private String reason;

    private LocalDateTime expireTime;

    private Integer hitCount;

    private LocalDateTime createTime;

    private String createUserName;
}
