# bsh-price-service

行情历史 / 实时报价 / WebSocket。默认端口 **8084**。  
层优先包树（对齐 LikoMind / `bsh-user-service`）。

## 包根

`com.baishuhui`（`PriceServiceApplication`）

```text
interfaces/price/controller|vo|ws
application/service/price/
infrastructure/db/mongo|memory · cache · security · config
```

领域：`bsh-price-service（含 domain 包）`  
契约：`bsh-price-client`（骨架）

## 启动

```bash
mvn -pl :bsh-price-service -am spring-boot:run -Dspring-boot.run.profiles=local
```
