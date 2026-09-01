package com.baishuhui.common.ddd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 聚合根基类。
 * <p>约束：所有聚合根必须继承本类；领域事件通过 registerEvent 收集，由应用层统一发布。</p>
 *
 * @param <ID> 聚合根标识类型
 * @author wei yz
 */
public abstract class AggregateRoot<ID> {

    private ID id;
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected AggregateRoot() {
    }

    protected AggregateRoot(ID id) {
        this.id = id;
    }

    /**
     * 聚合根唯一标识。
     */
    public ID getId() {
        return id;
    }

    protected void setId(ID id) {
        this.id = id;
    }

    /**
     * 注册领域事件（由聚合内部业务方法调用）。
     */
    protected void registerEvent(DomainEvent event) {
        // 忽略 null，避免调用方误传导致事件列表脏数据
        if (event != null) {
            this.domainEvents.add(event);
        }
    }

    /**
     * 拉取并清空已收集的领域事件。
     */
    public List<DomainEvent> pullDomainEvents() {
        // 空列表返回不可变空集合，避免无意义拷贝
        if (domainEvents.isEmpty()) {
            return Collections.emptyList();
        }
        List<DomainEvent> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return events;
    }

    /**
     * 只读查看当前已注册、尚未拉取的领域事件。
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
}
