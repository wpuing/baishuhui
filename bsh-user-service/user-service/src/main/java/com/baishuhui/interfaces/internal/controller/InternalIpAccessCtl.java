package com.baishuhui.interfaces.internal.controller;

import com.baishuhui.common.response.Result;
import com.baishuhui.user.vo.internal.AutoBanIpRequest;
import com.baishuhui.user.vo.internal.IpRuleSnapshotDTO;
import com.baishuhui.application.service.admin.IIpAccessAsvc;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 网关内部调用：规则快照与自动拉黑落库。不经公网网关路由。
 *
 * @author wei yz
 */
@RestController
@RequestMapping("/internal/ip")
@RequiredArgsConstructor
public class InternalIpAccessCtl {

    private final IIpAccessAsvc ipAccessAsvc;

    /**
     * 当前有效白名单 / 黑名单快照。
     */
    @GetMapping("/snapshot")
    public Result<IpRuleSnapshotDTO> snapshot() {
        return Result.success(ipAccessAsvc.snapshot());
    }

    /**
     * 网关检测到短时海量请求后自动拉黑。
     */
    @PostMapping("/auto-ban")
    public Result<Void> autoBan(@Valid @RequestBody AutoBanIpRequest request) {
        ipAccessAsvc.autoBan(request);
        return Result.success();
    }
}
