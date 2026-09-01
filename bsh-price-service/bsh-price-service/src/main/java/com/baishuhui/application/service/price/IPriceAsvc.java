package com.baishuhui.application.service.price;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.domain.price.entity.MarketPrice;
import com.baishuhui.domain.price.entity.vo.PriceSnapshot;
import com.baishuhui.domain.price.entity.vo.Unit;
import com.baishuhui.domain.price.repositories.IMarketPriceRepository;
import com.baishuhui.price.vo.PriceQuoteDTO;
import com.baishuhui.price.vo.PriceQuoteRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用服务接口（原 IPriceAsvc）。
 *
 * @author wei yz
 */
public interface IPriceAsvc {
    PriceQuoteDTO recordQuote(PriceQuoteRequest request);
    List<PriceQuoteDTO> listHistory(String sku, Integer limit);
    PriceQuoteDTO realtime(String sku);
}
