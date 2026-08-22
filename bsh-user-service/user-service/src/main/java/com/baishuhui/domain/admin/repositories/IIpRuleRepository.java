package com.baishuhui.domain.admin.repositories;

import com.baishuhui.domain.admin.entity.IpRuleEntity;
import com.baishuhui.domain.support.PageData;

import java.util.List;

/**
 * IP 访问规则仓储。
 *
 * @author wei yz
 */
public interface IIpRuleRepository {

    /**
     * 有效规则列表（投影字段，供 Redis / 快照）。
     */
    List<IpRuleEntity> listActive();

    /**
     * 分页查询。
     *
     * @param ruleType 可选 WHITELIST / BLACKLIST
     * @param pageNum  页码（从 1）
     * @param pageSize 每页条数
     * @return 分页数据
     */
    PageData<IpRuleEntity> page(String ruleType, int pageNum, int pageSize);

    /**
     * 按主键查询。
     */
    IpRuleEntity getById(String id);

    /**
     * 精确匹配 IP + 类型。
     */
    IpRuleEntity findExact(String ip, String ruleType);

    /**
     * 新增。
     */
    void insert(IpRuleEntity entity);

    /**
     * 更新。
     */
    int updateById(IpRuleEntity entity);

    /**
     * 逻辑删除。
     */
    int deleteById(String id);
}
