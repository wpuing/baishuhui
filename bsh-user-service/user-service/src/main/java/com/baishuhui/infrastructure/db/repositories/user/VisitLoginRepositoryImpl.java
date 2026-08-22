package com.baishuhui.infrastructure.db.repositories.user;

import com.baishuhui.domain.user.entity.VisitLoginEntity;
import com.baishuhui.domain.user.repositories.IVisitLoginRepository;
import com.baishuhui.infrastructure.db.mapper.admin.VisitLoginMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 登录访问流水仓储实现。
 *
 * @author wei yz
 */
@Repository
@RequiredArgsConstructor
public class VisitLoginRepositoryImpl implements IVisitLoginRepository {

    private final VisitLoginMapper visitLoginMapper;

    @Override
    public void insert(VisitLoginEntity entity) {
        visitLoginMapper.insert(entity);
    }

    @Override
    public long countBetween(LocalDateTime start, LocalDateTime end) {
        return visitLoginMapper.countBetween(start, end);
    }

    @Override
    public List<VisitLoginEntity> listBetween(LocalDateTime start, LocalDateTime end, int limit) {
        List<VisitLoginEntity> rows = visitLoginMapper.selectBetween(start, end, limit);
        return rows == null ? Collections.emptyList() : rows;
    }
}
