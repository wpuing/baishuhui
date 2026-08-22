package com.baishuhui.interfaces.supply.controller;

import com.baishuhui.supply.vo.SupplyInfoDTO;
import com.baishuhui.application.service.supply.ISupplyAsvc;
import com.baishuhui.common.response.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消费者侧供应 REST 接口。
 *
 * @author wei yz
 */
@RestController
@RequestMapping("/api/consumer")
@RequiredArgsConstructor
public class ConsumerSupplyCtl {
    private final ISupplyAsvc supplyAsvc;

    /**
     * 健康检查。
     */
    @GetMapping("/health")

    public Result<String> health() {
        return Result.success("consumer-ok");
    }

    /**
     * listPublished。
     */
    @GetMapping("/supplies")

    public Result<List<SupplyInfoDTO>> listPublished() {
        return supplyAsvc.listPublished();
    }

    /**
     * listAll。
     */
    @GetMapping("/supplies/all")

    public Result<List<SupplyInfoDTO>> listAll() {
        return supplyAsvc.listAll();
    }

    /**
     * detail。
     */
    @GetMapping("/supplies/{id}")

    public Result<SupplyInfoDTO> detail(@PathVariable("id") String id) {
        return supplyAsvc.detail(id);
    }
}
