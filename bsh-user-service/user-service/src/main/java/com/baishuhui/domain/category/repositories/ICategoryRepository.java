package com.baishuhui.domain.category.repositories;

import com.baishuhui.domain.category.entity.CategoryEntity;

import java.util.List;

/**
 * 品类仓储。
 *
 * @author wei yz
 */
public interface ICategoryRepository {

    /**
     * 启用中的品类，按 sort_no、名称排序。
     */
    List<CategoryEntity> listEnabled();
}
