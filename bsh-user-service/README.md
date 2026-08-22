# bsh-user-service

`user` 限界上下文（common / client / service）。

| 目录 | artifactId | 职责 |
|------|------------|------|
| `user-common` | `bsh-user-common` | VO、常量（`com.baishuhui.user.vo.*` / `user.constant`） |
| `user-client` | `bsh-user-client` | Feign 契约（`I*FeignService`） |
| `user-service` | `bsh-user-service` | 可运行微服务；含 `interfaces` / `application` / `domain` / `infrastructure` |

```bash
cd baishuhui-platform
mvn -pl :bsh-user-service -am spring-boot:run -Dspring-boot.run.profiles=local
```
