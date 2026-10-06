package com.baishuhui.domain.admin.repositories;

import com.baishuhui.domain.admin.entity.DashboardDailyEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 运营日报快照仓储。
 *
 * @author wei yz
 */
public interface IDashboardDailyRepository {

    void saveOrUpdate(DashboardDailyEntity entity);

    Optional<DashboardDailyEntity> findByStatDate(LocalDate statDate);

    List<DashboardDailyEntity> listBetween(LocalDate from, LocalDate to);
}
