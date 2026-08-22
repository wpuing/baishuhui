package com.baishuhui.infrastructure.db.repositories.admin;

import com.baishuhui.domain.admin.entity.SysConfigEntity;
import com.baishuhui.domain.admin.repositories.ISysConfigRepository;
import com.baishuhui.domain.support.PageData;
import com.baishuhui.infrastructure.db.mapper.admin.SysConfigMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 系统参数仓储实现。
 *
 * @author wei yz
 */
@Repository
@RequiredArgsConstructor
public class SysConfigRepositoryImpl implements ISysConfigRepository {

    private final SysConfigMapper sysConfigMapper;

    @Override
    public List<SysConfigEntity> listSyncRedis() {
        List<SysConfigEntity> list = sysConfigMapper.selectList(new LambdaQueryWrapper<SysConfigEntity>()
                .eq(SysConfigEntity::getSyncRedis, 1));
        return list == null ? Collections.emptyList() : list;
    }

    @Override
    public PageData<SysConfigEntity> page(String groupCode, int pageNum, int pageSize) {
        LambdaQueryWrapper<SysConfigEntity> w = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(groupCode)) {
            w.eq(SysConfigEntity::getGroupCode, groupCode.trim());
        }
        w.orderByAsc(SysConfigEntity::getGroupCode).orderByAsc(SysConfigEntity::getConfigKey);
        Page<SysConfigEntity> mp = sysConfigMapper.selectPage(new Page<>(pageNum, pageSize), w);
        if (mp == null || mp.getRecords() == null) {
            return new PageData<>(Collections.emptyList(), 0L);
        }
        return new PageData<>(mp.getRecords(), mp.getTotal());
    }

    @Override
    public SysConfigEntity getById(String id) {
        return sysConfigMapper.selectById(id);
    }

    @Override
    public boolean existsKey(String configKey, String excludeId) {
        LambdaQueryWrapper<SysConfigEntity> w = new LambdaQueryWrapper<SysConfigEntity>()
                .eq(SysConfigEntity::getConfigKey, configKey.trim());
        if (StringUtils.hasText(excludeId)) {
            w.ne(SysConfigEntity::getId, excludeId);
        }
        Long c = sysConfigMapper.selectCount(w);
        return c != null && c > 0;
    }

    @Override
    public void insert(SysConfigEntity entity) {
        sysConfigMapper.insert(entity);
    }

    @Override
    public int updateById(SysConfigEntity entity) {
        return sysConfigMapper.updateById(entity);
    }

    @Override
    public int deleteById(String id) {
        return sysConfigMapper.deleteById(id);
    }
}
