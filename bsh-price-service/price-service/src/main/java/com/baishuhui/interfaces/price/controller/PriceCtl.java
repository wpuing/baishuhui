package com.baishuhui.interfaces.price.controller;

import com.baishuhui.common.response.Result;
import com.baishuhui.price.vo.PriceQuoteDTO;
import com.baishuhui.application.service.price.IPriceAsvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 公开行情查询。
 *
 * @author wei yz
 */
@Tag(name = "行情")
@RestController
@RequestMapping("/api/price")
@RequiredArgsConstructor
public class PriceCtl {

    private final IPriceAsvc priceAsvc;

    /**
     * 历史行情。
     *
     * @param sku   品类 / SKU
     * @param limit 条数
     * @return 历史列表
     */
    @Operation(summary = "历史行情")
    @GetMapping("/history")
    public Result<List<PriceQuoteDTO>> history(
            @RequestParam("sku") String sku,
            @RequestParam(value = "limit", required = false) Integer limit) {
        return Result.success(priceAsvc.listHistory(sku, limit));
    }

    /**
     * 实时行情。
     *
     * @param sku 品类 / SKU
     * @return 实时报价
     */
    @Operation(summary = "实时行情")
    @GetMapping("/realtime/{sku}")
    public Result<PriceQuoteDTO> realtime(@PathVariable("sku") String sku) {
        return Result.success(priceAsvc.realtime(sku));
    }
}
