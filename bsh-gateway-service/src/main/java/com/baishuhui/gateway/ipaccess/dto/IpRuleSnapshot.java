package com.baishuhui.gateway.ipaccess.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 对齐 user-service {@code IpRuleSnapshotDTO}。
 *
 * @author wei yz
 */
@Data
public class IpRuleSnapshot {

    private List<String> whitelist = new ArrayList<>();

    private List<BlacklistEntry> blacklist = new ArrayList<>();

    /**
     * 黑名单项。
     */
    @Data
    public static class BlacklistEntry {

        private String ip;

        private Long expireEpochMilli;
    }
}
