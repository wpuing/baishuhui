package com.baishuhui.infrastructure.db.mapper.category;

import com.baishuhui.domain.category.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 品类字典 Mapper。
 *
 * @author wei yz
 */
@Mapper
public interface CategoryMapper extends BaseMapper<CategoryEntity> {
}
