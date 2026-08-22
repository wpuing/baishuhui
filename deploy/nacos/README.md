# deploy/nacos/

本机 / 开发用 Nacos 资源模板（公开仓）。

| 文件 | 说明 |
| --- | --- |
| `docker-compose-nacos.yml` | 本地启动 Nacos 参考（**配置项已注释**，按需自行取消注释并填写） |
| `config/bsh-common.yaml` | 共享配置模板（**全文配置项已注释**） |

## 使用说明

1. 按本机环境编辑下方文件：取消需要的配置注释并填写主机 / 端口等（**勿提交真实生产地址与口令**）
2. 启动示例（填写完成后）：

```bash
cd baishuhui-platform
docker compose -f deploy/nacos/docker-compose-nacos.yml up -d
# 控制台默认见 compose 中注释的端口说明
```
