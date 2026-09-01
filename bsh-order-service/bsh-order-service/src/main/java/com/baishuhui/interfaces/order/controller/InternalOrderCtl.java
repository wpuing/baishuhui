package com.baishuhui.interfaces.order.controller;

import com.baishuhui.order.vo.CategoryRankDTO;
import com.baishuhui.application.service.order.IOrderAsvc;
import com.baishuhui.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单内部调用 REST 接口。
 *
 * @author wei yz
 */
@RestController
@RequestMapping("/internal/order")
@RequiredArgsConstructor
@Slf4j
public class InternalOrderCtl {
    private final IOrderAsvc orderAsvc;

    /**
     * categoryRanking。
     */
    @GetMapping("/rankings/categories")

    public Result<List<CategoryRankDTO>> categoryRanking(
            @RequestParam(value = "topN", defaultValue = "10") int topN) {
        log.info("categoryRanking topN={}", topN);
        return orderAsvc.categoryRanking(topN);
    }
}
