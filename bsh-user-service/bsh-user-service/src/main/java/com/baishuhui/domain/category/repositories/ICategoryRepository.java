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

    /**
     * 全部品类（含停用），按排序。
     */
    List<CategoryEntity> listAllOrdered();

    /**
     * 按主键查询。
     */
    CategoryEntity getById(String id);

    /**
     * 新增。
     */
    void insert(CategoryEntity entity);

    /**
     * 更新。
     */
    int updateById(CategoryEntity entity);

    /**
     * 逻辑删除。
     */
    int deleteById(String id);

    /**
     * 名称是否已存在（可排除自身）。
     */
    boolean existsName(String name, String excludeId);
}
