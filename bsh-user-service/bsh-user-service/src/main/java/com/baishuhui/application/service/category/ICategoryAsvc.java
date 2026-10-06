package com.baishuhui.application.service.category;

import com.baishuhui.user.vo.category.AdminCategoryDTO;
import com.baishuhui.user.vo.category.CategoryDTO;
import com.baishuhui.user.vo.category.UpsertCategoryRequest;

import java.util.List;

/**
 * 品类应用服务。
 *
 * @author wei yz
 */
public interface ICategoryAsvc {

    /**
     * 启用中的品类（公开下拉）。
     */
    List<CategoryDTO> listEnabled();

    /**
     * 管理端全部品类。
     */
    List<AdminCategoryDTO> listAll();

    /**
     * 新增品类。
     */
    AdminCategoryDTO create(UpsertCategoryRequest request);

    /**
     * 更新品类。
     */
    AdminCategoryDTO update(String id, UpsertCategoryRequest request);

    /**
     * 逻辑删除品类。
     */
    void delete(String id);
}
