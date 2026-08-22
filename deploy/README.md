# deploy/

部署与本地中间件相关目录（**非业务源码**）。

| 子目录 | 说明 | 可否进公开仓 |
| --- | --- | --- |
| `docker/` | 业务容器编排：`docker-compose.yml`、`Dockerfile`；密钥同目录 `.env` | **禁止**（含 compose / Dockerfile / `.env*`） |
| `nacos/` | 本机 Nacos Compose + 配置模板 | **可以**（文件可公开；**配置项须注释掉**，不给真实值） |
| `local-cicd/` | 本机 GitLab / Jenkins 等机密与路径 | **禁止** |

密钥（仅本机 / 远程部署用）：复制 `deploy/docker/.env.example` → `.env`，填写 `BSH_JWT_SECRET`、`BSH_INTERNAL_TOKEN`。远程部署脚本会读取 `.env`；**切勿**把 `.env`、真实主机 IP 或未注释的生产配置推到公开仓。

业务源码在仓库根：`bsh-common` · `bsh-gateway-service` · `bsh-{user,supply,order,price,home}-service`（各含 `{ctx}-common` / `client` / `service`）。
