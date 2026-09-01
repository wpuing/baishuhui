package com.baishuhui.application.service.notify;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.response.Result;
import com.baishuhui.common.util.IdUtil;
import com.baishuhui.domain.notify.entity.NotificationEntity;
import com.baishuhui.domain.notify.repositories.INotificationRepository;
import com.baishuhui.user.vo.notify.CreateNotificationCommand;
import com.baishuhui.user.vo.notify.NotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 站内消息编排：写入、列表、已读。
 *
 * @author wei yz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationAsvcImpl implements INotificationAsvc {

    private final INotificationRepository notificationRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(CreateNotificationCommand command) {
        if (command == null || !StringUtils.hasText(command.getUserId())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "消息接收人不能为空");
        }
        NotificationEntity entity = new NotificationEntity();
        entity.setId(IdUtil.nextId());
        entity.setUserId(command.getUserId().trim());
        entity.setMsgType(command.getMsgType());
        entity.setTitle(command.getTitle());
        entity.setContent(command.getContent());
        entity.setBizType(command.getBizType());
        entity.setBizId(command.getBizId());
        entity.setReadFlag(0);
        notificationRepository.save(entity);
        log.info("notification created userId={} type={} bizId={}",
                entity.getUserId(), entity.getMsgType(), entity.getBizId());
    }

    @Override
    public Result<List<NotificationDTO>> listMine(String userId, int limit) {
        assertUser(userId);
        List<NotificationDTO> list = notificationRepository.listByUser(userId, limit).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result<Long> unreadCount(String userId) {
        assertUser(userId);
        return Result.success(notificationRepository.countUnread(userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> markRead(String userId, String id) {
        assertUser(userId);
        if (!StringUtils.hasText(id)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "消息 id 不能为空");
        }
        notificationRepository.markRead(id.trim(), userId);
        return Result.success(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> markAllRead(String userId) {
        assertUser(userId);
        notificationRepository.markAllRead(userId);
        return Result.success(null);
    }

    private static void assertUser(String userId) {
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
    }

    private NotificationDTO toDto(NotificationEntity e) {
        return NotificationDTO.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .msgType(e.getMsgType())
                .title(e.getTitle())
                .content(e.getContent())
                .bizType(e.getBizType())
                .bizId(e.getBizId())
                .readFlag(e.getReadFlag())
                .createTime(e.getCreateTime())
                .build();
    }
}
