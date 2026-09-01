package com.baishuhui.infrastructure.db.repositories.notify;

import com.baishuhui.domain.notify.entity.NotificationEntity;
import com.baishuhui.domain.notify.repositories.INotificationRepository;
import com.baishuhui.infrastructure.db.mapper.notify.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 站内消息仓储实现。
 *
 * @author wei yz
 */
@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements INotificationRepository {

    private final NotificationMapper notificationMapper;

    @Override
    public void save(NotificationEntity entity) {
        notificationMapper.insert(entity);
    }

    @Override
    public Optional<NotificationEntity> findById(String id) {
        return Optional.ofNullable(notificationMapper.selectById(id));
    }

    @Override
    public List<NotificationEntity> listByUser(String userId, int limit) {
        int size = Math.min(Math.max(limit, 1), 100);
        return notificationMapper.selectByUser(userId, size);
    }

    @Override
    public long countUnread(String userId) {
        return notificationMapper.countUnread(userId);
    }

    @Override
    public void markRead(String id, String userId) {
        notificationMapper.markRead(id, userId);
    }

    @Override
    public void markAllRead(String userId) {
        notificationMapper.markAllRead(userId);
    }
}
