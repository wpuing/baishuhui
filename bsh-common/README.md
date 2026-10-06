# bsh-common

公共基础库 + Web 自动配置（已合并原 `bsh-web-starter`）。

## 包结构

```text
com.baishuhui.common
├── constant/       # ErrorCode
├── ddd/            # AggregateRoot、DomainEvent、IRepository、ValueObject
├── exception/      # BusinessException
├── persistence/    # BaseEntity、OperatorContext
├── response/       # Result<T>
└── util/           # DateUtil、MoneyUtil、IdUtil

com.baishuhui.web
├── BshWebAutoConfiguration
├── feign/          # LoggingFeignClient、BshFeignLoggingAutoConfiguration（OpenFeign  classpath 生效）
├── exception/      # GlobalExceptionHandler
├── knife4j/
└── spi/            # Knife4jOpenApiCustomizer、ExceptionMessageCustomizer
```

## 约定

- 业务服务只依赖本模块即可获得统一异常与 Knife4j
- OpenFeign 服务自动启用 Feign 耗时日志（`bsh.feign.logging.enabled=true`，默认开）
- 错误码统一走 `ErrorCode`
- MySQL 实体继承 `BaseEntity`；主键 32 位无横线 UUID（`IdUtil`）
- 禁止在本模块写具体业务 Ctl
