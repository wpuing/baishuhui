package com.baishuhui.domain.user.repositories;

import com.baishuhui.domain.user.entity.VisitLoginEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录访问流水仓储。
 *
 * @author wei yz
 */
public interface IVisitLoginRepository {

    /**
     * 新增流水。
     */
    void insert(VisitLoginEntity entity);

    /**
     * 时间窗内次数（含起不含止）。
     */
    long countBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 时间窗内流水，最新在前。
     */
    List<VisitLoginEntity> listBetween(LocalDateTime start, LocalDateTime end, int limit);
}
