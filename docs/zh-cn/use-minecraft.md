# Minecraft 插件使用指南

InfiniteBot4-minecraft 是运行在 Bukkit/Spigot 服务端中的 QQ 机器人扩展插件。

- OneBot/QQ 连接能力由 **Perpetua** 提供
- InfiniteBot4 负责：连接管理、事件分发、扩展（Expansion）加载、数据持久化与分布式能力封装

---

## 环境要求

- **服务端**：Bukkit/Spigot（插件以 Spigot API 编译：`1.16.4-R0.1-SNAPSHOT`）
- **Java**：建议 Java 8+（高版本 Java 若出现反射限制报错，见下文）

### Java 9+ 反射限制（可选）

在部分高版本 Java 环境中，可能需要添加 JVM 参数以允许反射调用（若你未遇到相关报错可忽略）：

```cmd
--add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.lang.invoke=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED
```

---

## 安装与部署

### 1) 安装 Perpetua（前置）

InfiniteBot4 依赖 Perpetua 提供 OneBot 连接与事件能力，请先完成 Perpetua 的安装与登录：

- <https://iunlimit.github.io/perpetua/#/zh-cn/user/quick-start>

### 2) 安装 InfiniteBot4-minecraft

1. 将构建产物（shadowJar）放入服务器 `plugins/`：

   - 文件名形如：`infinitebot4-minecraft-all-<version>.jar`

2. 启动服务器，首次启动会生成：

   - `plugins/InfiniteBot4/config.yml`
   - `plugins/InfiniteBot4/player_data.yml`（yaml 数据模式下）
   - `plugins/InfiniteBot4/expansions/`（附属目录）

### 3) 配置 `config.yml`

配置文件示例见插件 jar 内置的默认 `config.yml`（以你实际生成的 `plugins/InfiniteBot4/config.yml` 为准）。

关键节点：

- `main.admins`：管理员 QQ（OP 权限外，业务附属也常用此名单做鉴权）
- `main.groups`：监听群列表（用于过滤事件来源）
- `database.type`：`yaml` / `mysql`
- `connection.webapi.host/port`：Perpetua **WebAPI** 服务地址（用于建立管理连接）
- `connection.authorization`：如 Perpetua/OneBot 侧启用了 token 鉴权则填写
- `connection.name`：客户端名（建议全局唯一）
- `connection.master`：主节点标识（多服场景建议仅一个为 true）

> 注意：
>
> - `database` 相关配置属于启动期读取，修改后需重启生效。
> - `connection.webapi.*` 指向的是 **WebAPI 端口**；WebSocket 端口由 Perpetua 在 WebAPI 连接建立后注册并用于事件分发，你通常不需要在本插件侧单独填写 WebSocket 端口。

---

## 附属（Expansion）安装

内部附属 jar 放置路径：

- `plugins/InfiniteBot4/expansions/*.jar`

附属若包含配置文件资源（如 `config.yml`），会在首次加载后释放到：

- `plugins/InfiniteBot4/expansions/<ExpansionName>/`

---

## 指令（OP 可用）

主命令：

- `/ib4`（别名）
- `/InfiniteBot4`

```text
ib4:
 ├── help: 获取帮助
 ├── check: 检查账号连接与登录信息
 ├── reload: 重载本体 config.yml（仅重载内容，不重建数据库连接等组件）
 ├── expansions
 │   ├── list: 列出所有已加载附属的标识符（name-version::author）
 │   └── reload: 重载 expansions 目录下所有内部附属（外部持久化附属默认跳过卸载）
 ├── load
 │   └── <jar文件名>: 从 expansions 目录加载指定 jar
 └── unload
     └── <附属标识符>: 卸载指定附属（持久化 External Expansion 不允许通过指令卸载）
```

> 注意：由于历史原因，游戏内执行 `/ib4 help` 时输出的前缀仍为 `ib3:`，不影响实际使用。以上描述为各子命令的功能说明，实际 help 输出措辞可能略有不同。

---

## 目录结构

```text
plugins/
 └── InfiniteBot4/
     ├── config.yml
     ├── player_data.yml                # database.type=yaml 时存在
     └── expansions/
         ├── <some-expansion>.jar       # 内部附属 jar
         └── <ExpansionName>/           # 附属配置目录（自动生成）
             └── config.yml
```

---

## 附属下载（可选）

官方/推荐附属仓库：

- <https://github.com/IllTamer/infinitebot4-expansion>

---

## 关于 IB3 历史遗留痕迹

InfiniteBot4 由 InfiniteBot3 演进而来，当前版本中仍有部分历史遗留标识未完全迁移，你可能在以下位置看到 `IB3`/`InfiniteBot3` 字样：

- 游戏内 `/ib4 help` 输出前缀为 `ib3:`
- Bungee 端命令注册名为 `InfiniteBot3`/`ib3`
- `config.yml` 文件头部 ASCII Art 显示 `Bot 3`
- MySQL 数据库表名为 `infinite_bot_3`

这些均不影响功能使用，后续版本会逐步统一。

---

## 下一步

- 附属开发：见《附属（Expansion）开发指南》《附属能力与可用 API》
- 常见问题：见《常见问题与排障（FAQ）》
