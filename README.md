# 百蔬汇 (BaiShuHui)

百蔬汇：蔬菜供应、行情与交易协同平台。Spring Cloud Alibaba 微服务 + DDD 分层。

买家 / 农户 / 商家操作说明见 [用户手册.md](用户手册.md)（不含系统后台，公开页不展示账号密码）。交易闭环文案为 **结单**。

## 目录

```text
baishuhui-platform/
├── pom.xml
├── bsh-common/
├── bsh-gateway-service/
├── bsh-user-service/     # bsh-user-common · bsh-user-client · bsh-user-service
├── bsh-supply-service/
├── bsh-order-service/
├── bsh-price-service/
├── bsh-home-service/
└── deploy/nacos/         # 仅注释化模板；不含 docker / 远程密钥
```

每个限界上下文：`bsh-{ctx}-common` + `bsh-{ctx}-client` + `bsh-{ctx}-service`（领域在 service 的 `domain` 包）。

> 本公开快照**不含** `deploy/docker`、本机 CI、运维脚本、远程 profile 与真实主机信息。`deploy/nacos/` 仅提供**已注释**的配置骨架。

## 服务

| 服务 | 端口 | 说明 |
|------|------|------|
| `bsh-gateway-service` | 8080 | API 网关、JWT 透传 |
| `bsh-user-service` | 8081 | 用户 / 认证 / 钱包 / 菜单 |
| `bsh-supply-service` | 8082 | 供应发布、仓库 |
| `bsh-order-service` | 8083 | 定金 / 确认 / 结单 |
| `bsh-price-service` | 8084 | 行情（含 WebSocket） |
| `bsh-home-service` | 8085 | 首页聚合 |

## 快速开始

1. 准备 JDK 17+、Maven 3.9+、Nacos / MySQL / MongoDB / Redis（按需）
2. 使用 `local` / `standalone` profile；数据库等凭据仅放本机配置，**勿提交真实密钥**
3. 编译：`mvn -DskipTests package`

## License

Private / educational demo — change secrets before any real deployment.
