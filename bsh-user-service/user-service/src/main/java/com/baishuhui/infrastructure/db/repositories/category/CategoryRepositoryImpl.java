package com.baishuhui.infrastructure.db.repositories.category;

import com.baishuhui.domain.category.entity.CategoryEntity;
import com.baishuhui.domain.category.repositories.ICategoryRepository;
import com.baishuhui.infrastructure.db.mapper.category.CategoryMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}
