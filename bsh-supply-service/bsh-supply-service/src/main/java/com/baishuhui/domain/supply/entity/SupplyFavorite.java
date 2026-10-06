package com.baishuhui.domain.supply.entity;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 供应收藏。
 *
 * @author wei yz
 */
@Getter
public class SupplyFavorite {

    private final String id;
    private final String userId;
    private final String supplyId;
    private final LocalDateTime createdAt;

    public SupplyFavorite(String id, String userId, String supplyId, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.supplyId = supplyId;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }
}
