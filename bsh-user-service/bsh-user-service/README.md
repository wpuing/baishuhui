# bsh-user-service

用户 / 认证 / 管理端 / 地区 / 系统配置 / 钱包。默认端口 **8081**。  
层优先包树（对齐 LikoMind）+ `Ctl → Asvc → Dsvc → Repository → Mapper`。

## 包根

`com.baishuhui`（`UserServiceApplication`）

```text
interfaces/{auth,admin,area,category,menu,wallet,internal}/controller|vo
interfaces/config/
application/service/{auth,admin,area,category,menu,wallet}/
infrastructure/db/mapper|repositories · security · remote · cache
```

领域：`bsh-user-service（含 domain 包）`  
契约：`bsh-user-client`

## 启动

```bash
mvn -pl :bsh-user-service -am -DskipTests spring-boot:run -Dspring-boot.run.profiles=local
```
