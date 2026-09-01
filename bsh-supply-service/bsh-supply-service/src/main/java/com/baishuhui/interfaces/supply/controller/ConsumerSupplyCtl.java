package com.baishuhui.interfaces.supply.controller;

import com.baishuhui.supply.vo.SupplyInfoDTO;
import com.baishuhui.application.service.supply.ISupplyAsvc;
import com.baishuhui.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ConsumerSupplyCtl {
    private final ISupplyAsvc supplyAsvc;

    /**
     * 健康检查。
     */
    @GetMapping("/health")

    public Result<String> health() {
        log.info("health invoked");
        return Result.success("consumer-ok");
    }

    /**
     * listPublished。
     */
    @GetMapping("/supplies")

    public Result<List<SupplyInfoDTO>> listPublished() {
        log.info("listPublished invoked");
        return supplyAsvc.listPublished();
    }

    /**
     * listAll。
     */
    @GetMapping("/supplies/all")
    public Result<List<SupplyInfoDTO>> listAll() {
        log.info("listAll invoked");
        return supplyAsvc.listAll();
    }

    /**
     * 按品类 / 产地关键字 / 状态筛选。
     */
    @GetMapping("/supplies/query")
    public Result<List<SupplyInfoDTO>> query(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String status) {
        log.info("query supplies category={} location={} status={}", category, location, status);
        return supplyAsvc.listFiltered(category, location, status);
    }

    /**
     * detail。
     */
    @GetMapping("/supplies/{id}")
    public Result<SupplyInfoDTO> detail(@PathVariable("id") String id) {
        log.info("detail invoked");
        return supplyAsvc.detail(id);
    }
}
