# bsh-supply-service

`supply` 限界上下文（common / client / service）。

| 目录 | artifactId | 职责 |
|------|------------|------|
| `bsh-supply-common` | `bsh-supply-common` | VO、常量（`com.baishuhui.supply.vo` / `supply.constant`） |
| `bsh-supply-client` | `bsh-supply-client` | Feign 契约 |
| `bsh-supply-service` | `bsh-supply-service` | 可运行微服务；含四层包（domain 在 service 内） |

```bash
cd baishuhui-platform
mvn -pl :bsh-supply-service -am spring-boot:run -Dspring-boot.run.profiles=local
```
