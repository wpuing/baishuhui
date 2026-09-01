package com.baishuhui.interfaces.internal.controller;

import com.baishuhui.application.service.notify.INotificationAsvc;
import com.baishuhui.common.response.Result;
import com.baishuhui.user.vo.notify.CreateNotificationCommand;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内部写站内消息：供订单等服务在交易节点调用。
 *
 * @author wei yz
 */
@Hidden
@RestController
@RequestMapping("/internal/notifications")
@RequiredArgsConstructor
@Slf4j
public class InternalNotificationCtl {

    private final INotificationAsvc notificationAsvc;

    @PostMapping
    public Result<Void> create(@Valid @RequestBody CreateNotificationCommand command) {
        log.info("internal notification userId={} type={}", command.getUserId(), command.getMsgType());
        notificationAsvc.create(command);
        return Result.success(null);
    }
}
