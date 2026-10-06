# bsh-supply-service

供应发布 / 改货 / 锁定 / 商家仓库。默认端口 **8082**。  
层优先包树（对齐 LikoMind / `bsh-user-service`）。

## 包根

`com.baishuhui`（`SupplyServiceApplication`）

```text
interfaces/{supply,warehouse,file}/controller|vo
interfaces/config/
application/service/{supply,warehouse,file}/
infrastructure/db/mongo|memory · config
```

领域：`bsh-supply-service（含 domain 包）`（`com.baishuhui.domain.supply.*`）  
契约：`bsh-supply-client`

## 安全要点

- 商家供应 / 仓库接口须带网关写入的 `X-User-Id`，且与商家 id 一致
- `/internal/supply/**` 须头 `X-Bsh-Internal-Token`（`bsh.internal.token` / `BSH_INTERNAL_TOKEN`）

## 启动

```bash
mvn -pl :bsh-supply-service -am spring-boot:run -Dspring-boot.run.profiles=local
```
