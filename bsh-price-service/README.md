# bsh-price-service

`price` 限界上下文（common / client / service）。

| 目录 | artifactId | 职责 |
|------|------------|------|
| `bsh-price-common` | `bsh-price-common` | VO（`com.baishuhui.price.vo`） |
| `bsh-price-client` | `bsh-price-client` | Feign 契约 |
| `bsh-price-service` | `bsh-price-service` | 可运行微服务；含四层包（domain 在 service 内） |

```bash
cd baishuhui-platform
mvn -pl :bsh-price-service -am spring-boot:run -Dspring-boot.run.profiles=local
```
