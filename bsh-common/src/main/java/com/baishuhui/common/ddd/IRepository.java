package com.baishuhui.common.ddd;

import java.util.Optional;

/**
 * 仓储通用接口。
 * <p>约束：仓储接口仅定义在领域层，禁止出现任何技术注解（@Mapper、@Repository 等）。</p>
 *
 * @param <T>  聚合根类型
 * @param <ID> 聚合根标识类型
 * @author wei yz
 */
public interface IRepository<T extends AggregateRoot<ID>, ID> {

    /**
     * 按标识加载聚合；不存在时返回 empty。
     */
    Optional<T> findById(ID id);

    /**
     * 持久化聚合（新增或更新由实现决定）。
     */
    void save(T aggregate);

    /**
     * 按标识删除聚合。
     */
    void remove(ID id);
}
