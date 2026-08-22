package com.baishuhui.domain.area.repositories;

import com.baishuhui.domain.area.entity.AreaEntity;

import java.util.List;

/**
 * 地区仓储。
 *
 * @author wei yz
 */
public interface IAreaRepository {

    /**
     * 全部地区（层级、排序升序）。
     */
    List<AreaEntity> listAllOrdered();

    /**
     * 按主键查询。
     */
    AreaEntity getById(String id);

    /**
     * 新增。
     */
    void insert(AreaEntity entity);

    /**
     * 更新。
     */
    int updateById(AreaEntity entity);

    /**
     * 逻辑删除。
     */
    int deleteById(String id);

    /**
     * 编码是否已存在（可排除自身）。
     */
    boolean existsCode(String code, String excludeId);

    /**
     * 下级地区数量。
     */
    long countByParentId(String parentId);

    /**
     * 名称模糊匹配一条。
     */
    AreaEntity findFirstByNameLike(String namePart);

    /**
     * 省级名称模糊匹配一条。
     */
    AreaEntity findFirstProvinceByNameLike(String namePart);
}
