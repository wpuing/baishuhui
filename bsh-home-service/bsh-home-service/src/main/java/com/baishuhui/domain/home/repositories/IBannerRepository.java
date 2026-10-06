package com.baishuhui.domain.home.repositories;

import com.baishuhui.domain.home.entity.Banner;

import java.util.List;

/**
 * Banner 仓储接口。
 *
 * @author wei yz
 */
public interface IBannerRepository {

    /**
     * 按位置查询启用中的 Banner（权重降序）。
     *
     * @param position 位置
     * @return 列表
     */
    List<Banner> listEnabledByPosition(String position);

    /**
     * 全部 Banner。
     *
     * @return 列表
     */
    List<Banner> listAll();

    /**
     * 按 id 查询。
     *
     * @param id 主键
     * @return 实体，不存在则 null
     */
    Banner getById(String id);

    /**
     * 新增或更新。
     *
     * @param banner 实体
     * @return 保存后实体
     */
    Banner save(Banner banner);

    /**
     * 是否存在。
     *
     * @param id 主键
     * @return 是否存在
     */
    boolean existsById(String id);

    /**
     * 按 id 删除。
     *
     * @param id 主键
     */
    void deleteById(String id);
}
