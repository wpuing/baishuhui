package com.baishuhui.client.user.feign;

import com.baishuhui.common.response.Result;
import com.baishuhui.user.vo.notify.CreateNotificationCommand;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 站内消息内部 Feign。
 *
 * @author wei yz
 */
@FeignClient(name = "bsh-user-service", contextId = "notificationFeignClient", url = "${bsh.services.user:}")
public interface INotificationFeignService {

    @PostMapping("/internal/notifications")
    Result<Void> create(@Valid @RequestBody CreateNotificationCommand command);
}
