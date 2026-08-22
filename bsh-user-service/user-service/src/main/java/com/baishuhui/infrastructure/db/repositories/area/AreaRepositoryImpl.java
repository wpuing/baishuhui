package com.baishuhui.infrastructure.db.repositories.area;

import com.baishuhui.domain.area.entity.AreaEntity;
import com.baishuhui.domain.area.repositories.IAreaRepository;
import com.baishuhui.infrastructure.db.mapper.area.AreaMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 地区仓储实现。
 *
 * @author wei yz
 */
@Repository
@RequiredArgsConstructor
public class AreaRepositoryImpl implements IAreaRepository {

    private final AreaMapper areaMapper;

    @Override
    public List<AreaEntity> listAllOrdered() {
        List<AreaEntity> list = areaMapper.selectList(new LambdaQueryWrapper<AreaEntity>()
                .orderByAsc(AreaEntity::getLevel)
                .orderByAsc(AreaEntity::getSortNo)
                .orderByAsc(AreaEntity::getCode));
        return list == null ? Collections.emptyList() : list;
    }

    @Override
    public AreaEntity getById(String id) {
        return areaMapper.selectById(id);
    }

    @Override
    public void insert(AreaEntity entity) {
        areaMapper.insert(entity);
    }

    @Override
    public int updateById(AreaEntity entity) {
        return areaMapper.updateById(entity);
    }

    @Override
    public int deleteById(String id) {
        return areaMapper.deleteById(id);
    }

    @Override
    public boolean existsCode(String code, String excludeId) {
        LambdaQueryWrapper<AreaEntity> w = new LambdaQueryWrapper<AreaEntity>()
                .eq(AreaEntity::getCode, code);
        if (StringUtils.hasText(excludeId)) {
            w.ne(AreaEntity::getId, excludeId);
        }
        Long c = areaMapper.selectCount(w);
        return c != null && c > 0;
    }

    @Override
    public long countByParentId(String parentId) {
        Long c = areaMapper.selectCount(new LambdaQueryWrapper<AreaEntity>()
                .eq(AreaEntity::getParentId, parentId));
        return c == null ? 0L : c;
    }

    @Override
    public AreaEntity findFirstByNameLike(String namePart) {
        return areaMapper.selectOne(new LambdaQueryWrapper<AreaEntity>()
                .like(AreaEntity::getName, namePart)
                .last("LIMIT 1"));
    }

    @Override
    public AreaEntity findFirstProvinceByNameLike(String namePart) {
        return areaMapper.selectOne(new LambdaQueryWrapper<AreaEntity>()
                .like(AreaEntity::getName, namePart)
                .eq(AreaEntity::getLevel, 1)
                .last("LIMIT 1"));
    }
}
