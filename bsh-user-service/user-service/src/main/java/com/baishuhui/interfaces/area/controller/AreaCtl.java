package com.baishuhui.interfaces.area.controller;

import com.baishuhui.common.response.Result;
import com.baishuhui.user.vo.admin.AreaDTO;
import com.baishuhui.user.vo.admin.IpAreaHintDTO;
import com.baishuhui.application.service.area.IAreaAsvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 公开地区接口：注册下拉与 IP 提示。
 *
 * @author wei yz
 */
@Tag(name = "地区")
@RestController
@RequestMapping("/api/areas")
@RequiredArgsConstructor
public class AreaCtl {

    private final IAreaAsvc areaAsvc;

    @Operation(summary = "地区树")
    @GetMapping("/tree")
    public Result<List<AreaDTO>> tree() {
        return Result.success(areaAsvc.tree());
    }

    @Operation(summary = "按 IP 预填地区提示")
    @GetMapping("/ip-hint")
    public Result<IpAreaHintDTO> ipHint(HttpServletRequest request) {
        return Result.success(areaAsvc.ipHint(resolveClientIp(request)));
    }

    private static String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(realIp)) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }
}
