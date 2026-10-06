package com.baishuhui.infrastructure.db.repositories.category;

import com.baishuhui.domain.category.entity.CategoryEntity;
import com.baishuhui.domain.category.repositories.ICategoryRepository;
import com.baishuhui.infrastructure.db.mapper.category.CategoryMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 品类仓储实现。
 *
 * @author wei yz
 */
@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements ICategoryRepository {

    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryEntity> listEnabled() {
        List<CategoryEntity> rows = categoryMapper.selectList(
                new LambdaQueryWrapper<CategoryEntity>()
                        .eq(CategoryEntity::getEnabled, 1)
                        .orderByAsc(CategoryEntity::getSortNo)
                        .orderByAsc(CategoryEntity::getName));
        return rows == null ? Collections.emptyList() : rows;
    }

    @Override
    public List<CategoryEntity> listAllOrdered() {
        List<CategoryEntity> rows = categoryMapper.selectList(
                new LambdaQueryWrapper<CategoryEntity>()
                        .orderByAsc(CategoryEntity::getSortNo)
                        .orderByAsc(CategoryEntity::getName));
        return rows == null ? Collections.emptyList() : rows;
    }

    @Override
    public CategoryEntity getById(String id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public void insert(CategoryEntity entity) {
        categoryMapper.insert(entity);
    }

    @Override
    public int updateById(CategoryEntity entity) {
        return categoryMapper.updateById(entity);
    }

    @Override
    public int deleteById(String id) {
        return categoryMapper.deleteById(id);
    }

    @Override
    public boolean existsName(String name, String excludeId) {
        LambdaQueryWrapper<CategoryEntity> q = new LambdaQueryWrapper<CategoryEntity>()
                .eq(CategoryEntity::getName, name);
        if (StringUtils.hasText(excludeId)) {
            q.ne(CategoryEntity::getId, excludeId);
        }
        return categoryMapper.selectCount(q) > 0;
    }
}
