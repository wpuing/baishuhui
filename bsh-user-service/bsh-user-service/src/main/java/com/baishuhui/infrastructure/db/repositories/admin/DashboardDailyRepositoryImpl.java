package com.baishuhui.infrastructure.db.repositories.admin;

import com.baishuhui.domain.admin.entity.DashboardDailyEntity;
import com.baishuhui.domain.admin.repositories.IDashboardDailyRepository;
import com.baishuhui.infrastructure.db.mapper.admin.DashboardDailyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 运营日报仓储实现。
 *
 * @author wei yz
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class DashboardDailyRepositoryImpl implements IDashboardDailyRepository {

    private final DashboardDailyMapper dashboardDailyMapper;

    @Override
    public void saveOrUpdate(DashboardDailyEntity entity) {
        DashboardDailyEntity exists = dashboardDailyMapper.selectByStatDate(entity.getStatDate());
        if (exists == null) {
            try {
                dashboardDailyMapper.insert(entity);
                return;
            } catch (DuplicateKeyException ex) {
                // 多实例并发首写：撞唯一键后改为更新
                log.warn("dashboard daily duplicate date={}, retry update", entity.getStatDate());
                exists = dashboardDailyMapper.selectByStatDate(entity.getStatDate());
                if (exists == null) {
                    throw ex;
                }
            }
        }
        entity.setId(exists.getId());
        dashboardDailyMapper.updateById(entity);
    }

    @Override
    public Optional<DashboardDailyEntity> findByStatDate(LocalDate statDate) {
        return Optional.ofNullable(dashboardDailyMapper.selectByStatDate(statDate));
    }

    @Override
    public List<DashboardDailyEntity> listBetween(LocalDate from, LocalDate to) {
        return dashboardDailyMapper.selectBetween(from, to);
    }
}
