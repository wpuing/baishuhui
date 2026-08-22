# bsh-home-service

`home` 限界上下文（common / client / service）。

| 目录 | artifactId | 职责 |
|------|------------|------|
| `home-common` | `bsh-home-common` | VO（`com.baishuhui.home.vo`） |
| `home-client` | `bsh-home-client` | Feign 契约 |
| `home-service` | `bsh-home-service` | 可运行微服务；含四层包（domain 在 service 内） |

```bash
cd baishuhui-platform
mvn -pl :bsh-home-service -am spring-boot:run -Dspring-boot.run.profiles=local
```
