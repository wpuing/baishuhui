# bsh-gateway-service

API 网关：路由、JWT 鉴权透传、Sentinel 限流、IP 访问控制。默认端口 **8080**。

## 职责

- 将 `/api/**` 路由到 user / supply / order / price / home
- `/api/price/**` → price-service；`/ws/price/**` WebSocket
- `/api/menus/**` → user-service；`/api/merchant/trades/**` → order-service
- `/api/files/**`、`/uploads/**` 转到 supply-service（本地读图/上传；生产远程 Nginx 直连腾讯图床，不经网关）
- 路径白名单放行登录、验证码、首页、图片上传与访问等公开接口
- 受保护前缀校验 Bearer Token（含 `/api/admin/**`、`/api/merchant/**`、消费者交易/钱包）；先剥离客户端伪造的 `X-User-*` 再由 JWT 写入
- `/api/merchant/warehouse`、`/stats` → order-service（成交列表）；仓位/库存/出入库仍走 supply-service
- 改密 / 删用户后 Redis cutoff 校验 JWT `iat`，作废令牌返回 401（登录 / 验证码 / 注册除外）
- **IP 限流 / 白名单 / 黑名单**：短时海量请求 429，达到阈值自动拉黑；名单由 user-service 管理

## 包根

`com.baishuhui.gateway`

```text
config/                 # Sentinel 等
security/               # AuthGlobalFilter、JwtTokenService
ipaccess/               # IP 限流、黑白名单、自动拉黑
```

## IP 防护

- 过滤器 `IpAccessGlobalFilter`（order=-200，先于 JWT）
- 白名单跳过限流；黑名单直接 403 `FORBIDDEN`
- 默认 60 秒窗口：普通接口 200 次 429、800 次自动拉黑；登录/验证码 30 / 80
- 名单每 15 秒从 `http://127.0.0.1:8081/internal/ip/snapshot` 刷新（不走公网网关）
- 配置前缀：`bsh.gateway.ip-access`

## 主要依赖

- Spring Cloud Gateway、Nacos Discovery、Sentinel、JJWT、Reactive Redis、`bsh-common`

## 启动

```bash
mvn -pl :bsh-gateway-service spring-boot:run -Dspring-boot.run.profiles=local
# standalone：直连本机各服务端口，无需 Nacos
```

统一入口：http://127.0.0.1:8080
