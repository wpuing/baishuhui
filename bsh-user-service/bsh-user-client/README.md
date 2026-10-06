# bsh-*-client（Feign 契约）

对齐 LikoMind `*-client`：每个限界上下文独立模块。

```text
com.baishuhui.client.{ctx}
├── feign/I*FeignService.java
└── vo/                     # 入参 / 出参（原 command + dto）
```

| 模块 | Feign |
| --- | --- |
| `bsh-user-client` | `IWalletFeignService` |
| `bsh-supply-client` | `ISupplyFeignService` |
| `bsh-order-client` | `IOrderFeignService` |
| `bsh-price-client` | `IPriceFeignService` |
| `bsh-home-client` | `IHomeFeignService` |

消费方 `@EnableFeignClients(basePackages = "com.baishuhui.client")`。
