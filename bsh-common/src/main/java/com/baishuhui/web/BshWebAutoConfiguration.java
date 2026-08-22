package com.baishuhui.web;

import com.baishuhui.web.exception.GlobalExceptionHandler;
import com.baishuhui.web.knife4j.Knife4jAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * Web 公共能力自动配置入口：异常处理 + Knife4j。
 *
 * @author wei yz
 */
@AutoConfiguration
@Import({GlobalExceptionHandler.class, Knife4jAutoConfiguration.class})
public class BshWebAutoConfiguration {
}
