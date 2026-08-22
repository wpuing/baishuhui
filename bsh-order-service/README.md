# bsh-order-service

`order` 限界上下文（common / client / service）。

| 目录 | artifactId | 职责 |
|------|------------|------|
| `order-common` | `bsh-order-common` | VO、常量（`com.baishuhui.order.vo` / `order.constant`） |
| `order-client` | `bsh-order-client` | Feign 契约 |
| `order-service` | `bsh-order-service` | 可运行微服务；含四层包（domain 在 service 内） |

```bash
cd baishuhui-platform
mvn -pl :bsh-order-service -am spring-boot:run -Dspring-boot.run.profiles=local
```
