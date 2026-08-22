# bsh-user-common

`user` 限界上下文公共层（与 `user-client` 同级）：HTTP/Feign VO、常量等，**不含**业务编排与仓储实现。

| 包 | 内容 |
| --- | --- |
| `com.baishuhui.user.vo.admin` / `auth` / `category` / `internal` / `wallet` | VO / Command / DTO |
| `com.baishuhui.user.constant` | `UserStatusConstants`、`WalletConstants`、`WalletChannels` |
