# bsh-home-service

首页聚合 / Banner 管理。默认端口 **8085**。

## 包根

`com.baishuhui`（`HomeServiceApplication`）

```text
interfaces/{home,banner}/controller|vo
interfaces/config/
application/service/{home,banner}/
infrastructure/db/mongo
```

- 领域：`bsh-home-service（含 domain 包）`（Banner + IBannerRepository）
- 契约：`bsh-home-client`；聚合依赖 `bsh-supply-client` / `bsh-order-client`

## 启动

```bash
mvn -pl :bsh-home-service -am spring-boot:run -Dspring-boot.run.profiles=local
```
