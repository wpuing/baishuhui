package com.baishuhui.user.vo.internal;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关拉取的 IP 规则快照。
 *
 * @author wei yz
 */
@Data
public class IpRuleSnapshotDTO {

    private List<String> whitelist = new ArrayList<>();

    private List<BlacklistEntry> blacklist = new ArrayList<>();

    /**
     * 黑名单条目：ip 可为单地址或 CIDR。
     */
    @Data
    public static class BlacklistEntry {

        private String ip;

        /** 过期毫秒时间戳；空表示永久 */
        private Long expireEpochMilli;
    }
}
