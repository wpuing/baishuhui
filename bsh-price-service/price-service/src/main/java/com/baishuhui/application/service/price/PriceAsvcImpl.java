package com.baishuhui.application.service.price;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.domain.price.entity.MarketPrice;
import com.baishuhui.domain.price.entity.vo.PriceSnapshot;
import com.baishuhui.domain.price.entity.vo.Unit;
import com.baishuhui.domain.price.repositories.IMarketPriceRepository;
import com.baishuhui.price.vo.PriceQuoteDTO;
import com.baishuhui.price.vo.PriceQuoteRequest;
import com.baishuhui.interfaces.price.ws.PricePushWebSocketHandler;
import com.baishuhui.infrastructure.cache.RealtimePriceRedisStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 行情录入、历史查询与实时读取。
 *
 * @author wei yz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PriceAsvcImpl implements IPriceAsvc {

    private static final int DEFAULT_LIMIT = 50;

    private final IMarketPriceRepository marketPriceRepository;
    private final PricePushWebSocketHandler pricePushWebSocketHandler;
    private final ObjectProvider<RealtimePriceRedisStore> redisStore;
    private final ObjectMapper objectMapper;

    @Value("${bsh.price.history-max-limit:200}")
    private int historyMaxLimit;

    /**
     * 记录一笔报价并广播 WebSocket。
     *
     * @param request 报价
     * @return 最新报价
     */
    @Override
    public PriceQuoteDTO recordQuote(PriceQuoteRequest request) {
        String sku = request.getSku().trim();
        BigDecimal price = request.getPrice();
        String unitCode = StringUtils.hasText(request.getUnit()) ? request.getUnit().trim() : "斤";
        Unit unit = new Unit(unitCode, unitCode);
        MarketPrice aggregate = marketPriceRepository.findBySku(sku)
                .orElseGet(() -> MarketPrice.create(sku, sku, unit, price));
        aggregate.updatePrice(price);
        marketPriceRepository.save(aggregate);
        PriceQuoteDTO dto = toDto(sku, aggregate.getLatest());
        broadcast(dto);
        return dto;
    }

    /**
     * SKU 历史，新在前。
     *
     * @param sku   品类
     * @param limit 条数
     * @return 历史
     */
    @Override
    public List<PriceQuoteDTO> listHistory(String sku, Integer limit) {
        if (!StringUtils.hasText(sku)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "sku 不能为空");
        }
        int size = limit == null || limit <= 0 ? DEFAULT_LIMIT : Math.min(limit, Math.max(1, historyMaxLimit));
        List<PriceSnapshot> rows = marketPriceRepository.listHistory(sku.trim(), size);
        List<PriceQuoteDTO> list = new ArrayList<>(rows.size());
        for (PriceSnapshot row : rows) {
            list.add(toDto(sku.trim(), row));
        }
        return list;
    }

    /**
     * 实时价：优先 Redis，否则最新历史。
     *
     * @param sku 品类
     * @return 实时报价，可能为空
     */
    @Override
    public PriceQuoteDTO realtime(String sku) {
        if (!StringUtils.hasText(sku)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "sku 不能为空");
        }
        String key = sku.trim();
        RealtimePriceRedisStore store = redisStore.getIfAvailable();
        if (store != null) {
            String json = store.get(key).orElse(null);
            if (StringUtils.hasText(json)) {
                return parseRealtime(key, json);
            }
        }
        List<PriceSnapshot> history = marketPriceRepository.listHistory(key, 1);
        if (history.isEmpty()) {
            return null;
        }
        return toDto(key, history.get(0));
    }

    private PriceQuoteDTO parseRealtime(String sku, String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            BigDecimal price = null;
            if (node.hasNonNull("price")) {
                price = new BigDecimal(node.get("price").asText());
            }
            String unit = node.path("unit").asText(null);
            if (!StringUtils.hasText(unit)) {
                unit = null;
            }
            return PriceQuoteDTO.builder()
                    .sku(sku)
                    .price(price)
                    .unit(unit)
                    .payload(json)
                    .build();
        } catch (Exception ex) {
            log.warn("realtime json parse skip sku={}", sku, ex);
            return PriceQuoteDTO.builder().sku(sku).payload(json).build();
        }
    }

    private PriceQuoteDTO toDto(String sku, PriceSnapshot snapshot) {
        if (snapshot == null) {
            return PriceQuoteDTO.builder().sku(sku).build();
        }
        String unit = snapshot.unit() == null ? "斤" : snapshot.unit().code();
        return PriceQuoteDTO.builder()
                .sku(sku)
                .price(snapshot.price())
                .unit(unit)
                .snapshotTime(snapshot.snapshotTime())
                .build();
    }

    private void broadcast(PriceQuoteDTO dto) {
        try {
            pricePushWebSocketHandler.broadcast(objectMapper.writeValueAsString(dto));
        } catch (JsonProcessingException ex) {
            log.warn("price ws json fail sku={}", dto.getSku(), ex);
        }
    }
}
