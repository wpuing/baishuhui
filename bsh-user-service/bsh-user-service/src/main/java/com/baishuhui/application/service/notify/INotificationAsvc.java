package com.baishuhui.application.service.notify;

import com.baishuhui.common.response.Result;
import com.baishuhui.user.vo.notify.CreateNotificationCommand;
import com.baishuhui.user.vo.notify.NotificationDTO;

import java.util.List;

/**
 * 站内消息应用服务。
 *
 * @author wei yz
 */
public interface INotificationAsvc {

    void create(CreateNotificationCommand command);

    Result<List<NotificationDTO>> listMine(String userId, int limit);

    Result<Long> unreadCount(String userId);

    Result<Void> markRead(String userId, String id);

    Result<Void> markAllRead(String userId);
}
