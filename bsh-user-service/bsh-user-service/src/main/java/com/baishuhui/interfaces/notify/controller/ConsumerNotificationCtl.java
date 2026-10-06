package com.baishuhui.interfaces.notify.controller;

import com.baishuhui.application.service.notify.INotificationAsvc;
import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.response.Result;
import com.baishuhui.user.vo.notify.NotificationDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户站内消息：列表、未读数、已读。
 *
 * @author wei yz
 */
@Tag(name = "站内消息")
@RestController
@RequestMapping("/api/consumer/notifications")
@RequiredArgsConstructor
@Slf4j
public class ConsumerNotificationCtl {

    private final INotificationAsvc notificationAsvc;

    @Operation(summary = "我的消息列表")
    @GetMapping
    public Result<List<NotificationDTO>> list(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestParam(defaultValue = "50") int limit) {
        log.info("notifications list userId={}", userId);
        return notificationAsvc.listMine(requireUser(userId), limit);
    }

    @Operation(summary = "未读消息数")
    @GetMapping("/unread-count")
    public Result<Long> unreadCount(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        log.info("notifications unread userId={}", userId);
        return notificationAsvc.unreadCount(requireUser(userId));
    }

    @Operation(summary = "标记单条已读")
    @PostMapping("/{id}/read")
    public Result<Void> markRead(
            @PathVariable("id") String id,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        log.info("notifications markRead id={} userId={}", id, userId);
        return notificationAsvc.markRead(requireUser(userId), id);
    }

    @Operation(summary = "全部已读")
    @PostMapping("/read-all")
    public Result<Void> markAllRead(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        log.info("notifications markAllRead userId={}", userId);
        return notificationAsvc.markAllRead(requireUser(userId));
    }

    private static String requireUser(String userId) {
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        return userId.trim();
    }
}
