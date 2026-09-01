package com.baishuhui.domain.notify.repositories;

import com.baishuhui.domain.notify.entity.NotificationEntity;

import java.util.List;
import java.util.Optional;

/**
 * 站内消息仓储。
 *
 * @author wei yz
 */
public interface INotificationRepository {

    void save(NotificationEntity entity);

    Optional<NotificationEntity> findById(String id);

    List<NotificationEntity> listByUser(String userId, int limit);

    long countUnread(String userId);

    void markRead(String id, String userId);

    void markAllRead(String userId);
}
