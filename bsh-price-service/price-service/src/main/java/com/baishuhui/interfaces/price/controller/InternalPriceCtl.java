package com.baishuhui.interfaces.price.controller;

import com.baishuhui.common.response.Result;
import com.baishuhui.price.vo.PriceQuoteDTO;
import com.baishuhui.price.vo.PriceQuoteRequest;
import com.baishuhui.application.service.price.IPriceAsvc;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内部成交报价：供订单结单调用。
 *
 * @author wei yz
 */
@Hidden
@RestController
@RequestMapping("/internal/price")
@RequiredArgsConstructor
public class InternalPriceCtl {

    private final IPriceAsvc priceAsvc;

    /**
     * 记录成交价。
     *
     * @param request 报价
     * @return 最新报价
     */
    @PostMapping("/quotes")
    public Result<PriceQuoteDTO> quote(@Valid @RequestBody PriceQuoteRequest request) {
        return Result.success(priceAsvc.recordQuote(request));
    }
}
