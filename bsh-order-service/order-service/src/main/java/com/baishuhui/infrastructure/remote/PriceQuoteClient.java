package com.baishuhui.infrastructure.remote;

import com.baishuhui.client.price.feign.IPriceFeignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 结单后向行情服务记一笔成交价；失败只打日志，不挡结单。
 *
 * @author wei yz
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PriceQuoteClient {

    private final IPriceFeignService priceFeignService;

    /**
     * 记录成交报价。
     *
     * @param sku   品类 / SKU
     * @param price 单价
     * @param unit  单位
     */
    public void recordDeal(String sku, BigDecimal price, String unit) {
        if (!StringUtils.hasText(sku) || price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        Map<String, Object> body = new LinkedHashMap<>(4);
        body.put("sku", sku.trim());
        body.put("price", price);
        body.put("unit", StringUtils.hasText(unit) ? unit.trim() : "斤");
        long start = System.currentTimeMillis();
        try {
            priceFeignService.quote(body);
            log.info("price quote ok sku={} price={} cost={}ms", sku, price, System.currentTimeMillis() - start);
        } catch (Exception ex) {
            log.warn("price quote skip sku={} cost={}ms err={}",
                    sku, System.currentTimeMillis() - start, ex.getMessage());
        }
    }
}
