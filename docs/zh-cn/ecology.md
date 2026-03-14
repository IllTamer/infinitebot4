# 生态

InfiniteBot4 的整体生态由三部分组成：

1. **Perpetua（OneBot 连接层）**：负责与 OneBot 实现建立连接、提供事件与 Web API。
2. **InfiniteBot4-minecraft（Bukkit 插件侧）**：在 Minecraft 服务器内提供连接托管、事件分发、扩展（Expansion）体系、数据与分布式封装。
3. **业务附属（Expansions）**：承载具体功能（如消息互通、验证、管理、经济联动等），通过扩展体系热加载。

---

## 1. Perpetua

- 仓库/文档：<https://github.com/IUnlimit/perpetua>
- 快速开始：<https://iunlimit.github.io/perpetua/#/zh-cn/user/quick-start>

Perpetua 提供的能力包括但不限于：

- OneBot WebSocket 连接
- OneBot Web API 调用封装（`OpenAPIHandling`）
- 广播/回调事件（用于分布式能力）

---

## 2. InfiniteBot4-minecraft

- 仓库：<https://github.com/IllTamer/infinitebot4>

核心特性：

- Bukkit 插件形态部署
- `config.yml` 管理连接/权限/数据源
- 独立类加载器的内部附属（jar 热加载/卸载）
- 外部附属（Hook）：允许其它 Bukkit 插件注册成为 expansion
- YAML / MySQL 两种玩家数据持久化
- 分布式事件处理器：多客户端数据收集与回调聚合

---

## 3. Expansions（业务附属）

- 内部附属：放入 `plugins/InfiniteBot4/expansions/`
- 外部附属：由其它 Bukkit 插件通过 `AbstractExternalExpansion#register` 注册

官方/推荐附属仓库：

- <https://github.com/IllTamer/infinitebot4-expansion>

推荐阅读：

- 《Minecraft 插件使用指南》
- 《附属（Expansion）开发指南》
- 《附属编写规范》
- 《附属能力与可用 API》
