# 项目设计规范

本文从"项目作者/维护者/附属开发者"的视角，描述 InfiniteBot4（Minecraft 侧）当前的实现结构、关键约束与推荐设计规范。

> 说明：InfiniteBot4 的 QQ/OneBot 能力由 **Perpetua SDK** 提供，插件侧主要负责：连接管理、事件分发、附属（Expansion）体系、数据持久化与分布式协作封装。

---

## 1. 模块与边界

当前工程结构：

- 根工程：`infinitebot4`
- 子工程：`minecraft`

`minecraft` 模块的职责：

1. **启动与生命周期**：Bukkit 插件入口 `BukkitBootstrap` 负责加载配置、建立 OneBot WebSocket 连接、启动状态巡检任务、加载 expansions。
2. **连接与状态维护**：`BotNettyHolder` + `StatusCheckRunner` 对 Perpetua 的连接与登录状态进行自愈。
3. **事件系统**：`EventExecutor` 将 Perpetua 事件（`com.illtamer.perpetua.sdk.event.Event`）分发给 expansions 注册的监听方法。
4. **附属（Expansion）体系**：独立类加载器、热加载/热卸载、外部附属（hook）注册、资源/配置文件落盘。
5. **数据持久化**：`PlayerDataRepository` 的 YAML/MySQL 双实现，满足单服与群组服共享数据两类场景。
6. **分布式协作**：`api.distribute` 包提供基于 Perpetua broadcast 的分布式事件/回调与数据收集模型。

插件不直接实现 OneBot 协议细节：

- OneBot Web API / WS 连接、广播、客户端列表等能力来自 Perpetua SDK。

---

## 2. 启动流程（Bukkit）

入口类：`com.illtamer.infinite.bot.minecraft.start.bukkit.BukkitBootstrap`。

启动时序（简化）：

1. `onLoad()`
   1. `StaticAPI.setInstance(instance = this)`
   2. `BotConfiguration.load(this)`：读取/生成 `plugins/InfiniteBot4/config.yml`，并初始化 PlayerDataRepository（yaml 或 mysql）。
   3. 创建 `BotNettyHolder(getLogger(), EventExecutor::dispatchListener)`，将事件分发器作为消费者传入；调用 `connect()` 开启 Perpetua WebSocket。
2. `onEnable()`
   1. `checkConnection()` 打印连接状态
   2. 周期任务：`StatusCheckRunner`（初始延迟 2 秒，每 30 秒检查登录与在线状态），异常则 `StaticAPI.reconnected()` 触发重连。
   3. 延迟 3 秒后加载 expansions：`expansionLoader.loadExpansions(false)`
   4. 注册命令与 Bukkit 事件：`/ib4`（主命令在 `plugin.yml` 中为 `InfiniteBot4`，别名 `ib4`）
3. `onDisable()`
   1. 关闭调度器 `BotScheduler.close()`
   2. 卸载 expansions `disableExpansions(false)`
   3. `BotConfiguration.saveAndClose()`：将玩家数据缓存写入（yaml 模式）
   4. 关闭 WebSocket 线程池

设计约束：

- 连接与事件分发是插件核心能力；expansions 的业务逻辑应尽量与连接/生命周期解耦。

---

## 3. 配置设计规范

### 3.1 插件本体配置（config.yml）

配置入口类：`BotConfiguration`。

配置结构：

- `main`
  - `admins`: 管理员 QQ 列表（用于附属/业务侧权限判断）
  - `groups`: 监听群列表（用于过滤事件来源）
- `database`
  - `type`: `yaml` 或 `mysql`
  - `config.mysql.*`: 完整 YAML 路径为 `database.config.mysql.{host|port|username|password|database}`
- `connection`
  - `name`: 当前客户端名（用于分布式/日志识别；建议全局唯一）
  - `master`: 主节点标识（群组服场景建议仅一个节点为 true）
  - `webapi.host` / `webapi.port`: **Perpetua WebAPI** 连接地址（管理连接建立后，由 Perpetua 注册并维护 WebSocket 用于事件分发）
  - `authorization`: OneBot/Perpetua 验证 token（若 OneBot 侧启用了鉴权）

重载语义：

- `BotConfiguration.reload()` **仅重载 config.yml 内容**，不会重建 repository / datasource 等"启动期组件"。
- `database.type` 与 mysql 参数属于"仅启动读取"的配置，修改后需重启服务器/插件生效。

### 3.2 附属配置文件

推荐使用 `ExpansionConfig` 或自动配置系统（见《附属能力与可用 API》《附属开发指南》）：

- 文件路径：`plugins/InfiniteBot4/expansions/<ExpansionName>/...`
- 配置落盘：由 `ExpansionConfig` 负责首次从 jar 资源释放与后续 reload/save
- 版本升级：`ExpansionConfig` 支持 `version` 自动迁移/备份（生成 `*.bak`）

规范建议：

- 为每个配置文件提供 `version` 字段，并在代码升级时同步提升版本号。
- 不要在 `onEnable()` 中频繁磁盘 I/O；必要时使用 `BotScheduler.IO_INTENSIVE` 异步处理。

---

## 4. Expansion 体系设计规范

InfiniteBot4 将"附属"作为一等公民，提供两类扩展方式：

1. **内部附属（Embedded Expansion）**
   - 以 jar 文件形式放入 `plugins/InfiniteBot4/expansions/`
   - 主类必须继承 `InfiniteExpansion`
   - 由 `PluginClassLoader` 独立类加载，可热加载/卸载
2. **外部附属（External Expansion / Hook）**
   - 当你已有一个 Bukkit 插件，希望与 InfiniteBot4 互操作时使用
   - 主类继承 `AbstractExternalExpansion`，并在你的插件 `onEnable()` 中 `register(plugin)`
   - 生命周期由 InfiniteBot4 管理，但 classloader 由你自己的插件提供

### 4.1 唯一标识符

每个 expansion 由三元组唯一标识：

- `name-version::author`

由 `ExpansionUtil.formatIdentifier()` 统一生成，作为 ExpansionLoader 的 key。

规范要求：

- `name` 不应包含 `-`，否则会影响解析（解析正则：`([^-]*)-(.*)::(.*)`）。
- `version` 建议语义化（如 `1.2.0` 或 `1.2.0-SNAPSHOT`）。
- `author` 建议保持稳定一致。

### 4.2 热加载边界

- 内部附属 jar 支持按文件加载、卸载并释放 classloader（`PluginClassLoader#close()`）。
- 卸载时会自动注销：
  - Perpetua 事件监听（EventExecutor 内部维护）
  - Bukkit Event（通过 HandlerList 统一反注册）
  - 自动配置类注册（Registration#removeAndStoreAutoConfigs）

规范建议：

- 不要在 class/static 块里启动线程/注册监听/打开连接。
- 所有资源（线程、连接、文件句柄）必须在 `onDisable()` 中释放。

---

## 5. 事件系统设计规范

事件源：Perpetua SDK 的 `Event`。

事件分发：`EventExecutor.dispatchListener(Event)` 会将事件投递到 `BotScheduler`（线程池）异步执行。

监听规范：

- 监听类实现 `com.illtamer.infinite.bot.minecraft.api.event.Listener`
- 监听方法：
  - 仅允许 **一个参数**
  - 参数类型必须是 `com.illtamer.perpetua.sdk.event.Event` 的子类
  - 使用 `@com.illtamer.infinite.bot.minecraft.api.event.EventHandler` 标注
  - 可配置 `priority`（从高到低轮询）

取消语义：

- 如果事件实现了 `com.illtamer.perpetua.sdk.event.Cancellable`，当其 `isCancelled()==true` 时后续监听将被跳过。

规范建议：

- 监听处理应做到"快进快出"：耗时任务请异步或分片处理。
- 若你做了"拦截式处理"（例如关键字命令），请及时 `setCancelled(true)`，避免被其它转发类附属重复处理。

---

## 6. 分布式设计规范（群组服/多端）

分布式能力：`api.distribute.DistributedEventProcessor`。

核心思想：

- 主动向其它客户端广播一个 payload
- 其它客户端执行 handler，并通过 callback 回传结果
- 本端在超时窗口内收集结果，组合成 `DistributedResult<T>`

关键约束：

- 需要在 Perpetua 中存在"多个客户端"（clientList）才有意义；否则自动降级为单机模式。
- 数据类型 `T` 必须可被 Gson 序列化/反序列化。
- 默认回调收集超时时间为 3 秒（源码内写死）；如有需要可在未来抽配置。

规范建议：

- 分布式 handler 必须幂等且可重入。
- 网络不稳定时要能容忍部分节点超时：通过 `failedClientList` 做降级策略。

---

## 7. 兼容性与版本规范

- Bukkit API：当前 `compileOnly spigot-api:1.16.4-R0.1-SNAPSHOT`
- Bungee API：当前 `compileOnly bungeecord-api:1.21-R0.1`（但 Bungee 启动器仍处于未完整接入状态）
- Java：Java 8+；Java 9+ 可能需要 `--add-opens` 参数（见 FAQ）。

---

## 8. 代码风格与工程约定（建议）

- 公共 API（面向附属开发者）优先放在：`com.illtamer.infinite.bot.minecraft.api` 包。
- 与实现强耦合/可能变更的内部逻辑放在：`configuration/expansion/manager/util` 等包，避免被附属直接依赖。
- 任何可能抛异常的外部依赖调用（网络/数据库）需要捕获并输出足够上下文。
