# bsh-order-service

定金预定 / 支付 / 确认 / 结单。默认端口 **8083**。  
层优先包树（对齐 LikoMind / `bsh-user-service`）。

## 包根

`com.baishuhui`（`OrderServiceApplication`）

```text
interfaces/order/controller|schedule
interfaces/config/
application/service/order/
infrastructure/db/mongo|memory · remote
```

领域：`bsh-order-service（含 domain 包）`  
契约：`bsh-order-client`；跨服务：`bsh-supply-client`、`bsh-user-client`

## 启动

```bash
mvn -pl :bsh-order-service -am spring-boot:run -Dspring-boot.run.profiles=local
```
