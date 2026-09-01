package com.baishuhui.domain.admin.repositories;

import com.baishuhui.domain.admin.entity.SysConfigEntity;
import com.baishuhui.domain.support.PageData;

import java.util.List;

/**
 * 系统参数仓储。
 *
 * @author wei yz
 */
public interface ISysConfigRepository {

    /**
     * 需同步 Redis 的参数列表。
     */
    List<SysConfigEntity> listSyncRedis();

    /**
     * 分页查询。
     *
     * @param groupCode 可选分组
     * @param pageNum   页码
     * @param pageSize  每页条数
     * @return 分页数据
     */
    PageData<SysConfigEntity> page(String groupCode, int pageNum, int pageSize);

    /**
     * 按主键查询。
     */
    SysConfigEntity getById(String id);

    /**
     * 配置键是否已存在（可排除自身）。
     */
    boolean existsKey(String configKey, String excludeId);

    /**
     * 新增。
     */
    void insert(SysConfigEntity entity);

    /**
     * 更新。
     */
    int updateById(SysConfigEntity entity);

    /**
     * 逻辑删除。
     */
    int deleteById(String id);
}
