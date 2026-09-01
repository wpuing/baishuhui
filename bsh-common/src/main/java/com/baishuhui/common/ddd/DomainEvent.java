package com.baishuhui.common.ddd;

import java.time.LocalDateTime;

/**
 * 领域事件标记接口。
 * <p>命名规范：{聚合根}{动作}Event，例：SupplyPublishedEvent</p>
 *
 * @author wei yz
 */
public interface DomainEvent {

    /**
     * 事件发生时间。
     */
    LocalDateTime occurredOn();
}
