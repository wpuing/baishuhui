package com.baishuhui.web.feign;

import feign.Client;
import feign.Request;
import feign.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Feign {@link Client} 装饰：记录 HTTP 方法、脱敏路径、状态码与耗时。
 *
 * @author wei yz
 */
public class LoggingFeignClient implements Client {

    private static final Logger log = LoggerFactory.getLogger(LoggingFeignClient.class);

    private final Client delegate;

    public LoggingFeignClient(Client delegate) {
        this.delegate = delegate;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        long startNs = System.nanoTime();
        String method = request.httpMethod().name();
        String url = FeignLogSupport.sanitizeUrl(request.url());
        try {
            Response response = delegate.execute(request, options);
            long costMs = (System.nanoTime() - startNs) / 1_000_000L;
            int status = response.status();
            if (status >= 500) {
                log.warn("feign {} {} status={} costMs={}", method, url, status, costMs);
            } else {
                log.info("feign {} {} status={} costMs={}", method, url, status, costMs);
            }
            return response;
        } catch (IOException ex) {
            long costMs = (System.nanoTime() - startNs) / 1_000_000L;
            log.warn("feign {} {} fail costMs={} err={}", method, url, costMs, ex.getMessage());
            throw ex;
        }
    }
}
