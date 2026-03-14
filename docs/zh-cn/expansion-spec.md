# 附属编写规范（Expansion Spec）

本文给出 InfiniteBot4-minecraft 附属（Expansion）建议遵循的工程规范与约束。

---

## 1. 术语

- **内部附属（Embedded Expansion）**：以 jar 放入 `plugins/InfiniteBot4/expansions/`，由 InfiniteBot4 的独立类加载器加载。
- **外部附属（External Expansion）**：你的 Bukkit 插件通过 hook 方式注册到 InfiniteBot4。
- **标识符（Identifier）**：`name-version::author`，是附属在系统中的唯一 key。

---

## 2. 命名与版本

### 2.1 ExpansionName

- 不允许包含 `-`（解析器以 `-` 切分 name/version）
- 建议使用 PascalCase（如 `ChatManager`、`DefenseManager`）

### 2.2 Version

- 建议语义化：`MAJOR.MINOR.PATCH`
- 允许使用 `-SNAPSHOT` 等后缀

### 2.3 Author

- 建议固定不变，用于协作与冲突排查

---

## 3. 目录与资源

### 3.1 文件放置

内部附属 jar：

- `plugins/InfiniteBot4/expansions/<your-expansion>.jar`

配置目录（自动生成）：

- `plugins/InfiniteBot4/expansions/<ExpansionName>/`

> 实际文件夹名优先取 `getExpansionName()`；若为空则回退到主类 `SimpleName`。

### 3.2 资源文件（必须打进 jar）

常见资源：

- `config.yml`
- `language-zh_CN.yml` 等语言文件

要求：

- 使用 `saveResource("config.yml", false)` 或 `new ExpansionConfig("config.yml", this)` 时，资源必须存在

---

## 4. 生命周期与线程

### 4.1 onEnable/onDisable 必须对称

- `onEnable()` 中创建的线程/定时任务/连接/缓存，必须在 `onDisable()` 中释放
- 不要在 `static` 块或字段初始化阶段启动长期任务（这会破坏热卸载）

### 4.2 不阻塞主线程

- 事件处理器运行在 `BotScheduler` 的线程池中（异步）
- 但你注册 Bukkit 事件时，仍可能运行在主线程；请避免执行网络/数据库/大 I/O

### 4.3 异常处理

- 任何外部调用（HTTP、数据库、文件）都要捕获异常并打印上下文信息
- 建议使用 `getLogger()` 输出结构化日志

---

## 5. 配置规范

### 5.1 版本字段

- 每个配置文件建议包含 `version: <int>`
- 当你调整配置结构时：
  - 同步提升代码侧的 version
  - 让 `ExpansionConfig` 自动备份并迁移

### 5.2 Key 命名

- 推荐使用 `kebab-case`（与 AutoLoadConfiguration 的默认字段映射一致）
- 多级节点用 `.` 表示（Bukkit YAML 常规路径）

---

## 6. 事件监听规范

- 监听类实现 `com.illtamer.infinite.bot.minecraft.api.event.Listener`
- 监听方法只允许 1 个参数，并且是 `com.illtamer.perpetua.sdk.event.Event` 子类
- 若你的逻辑属于"拦截/消费"型（如命令、关键字），应当在事件上设置 cancelled（若事件支持），避免其它附属重复处理

---

## 7. 分布式规范（可选）

当你使用 `DistributedEventProcessor`：

- handler 必须幂等（可能被重复触发）
- `T` 必须可 JSON 序列化
- 对失败节点要做降级：使用 `failedClientList`

---

## 8. 外部附属（Hook）规范

当你在自己的 Bukkit 插件中注册 External Expansion：

- 在 `onEnable()` 调用 `new YourExternalExpansion().register(this)`
- 在 `onDisable()` 主动 `unregister()` 是最佳实践（即便 InfiniteBot4 也会被动处理）
- 如果你的插件卸载时忘记注销，InfiniteBot4 会在 `PluginDisableEvent` 里自动帮你注销，但会输出警告日志

---

## 9. 发布与兼容性

- 目标兼容 Bukkit/Spigot 版本：建议与 InfiniteBot4 当前 compileOnly 版本对齐（1.16.4 API），避免使用更高版本新增 API
- 依赖声明：
  - 如果你是内部附属：以 maven/gradle 依赖 `com.illtamer.infinite.bot:minecraft:{version}` 进行编译
  - 如果你是外部附属：你的插件也应 compileOnly 引入上述依赖，避免运行时冲突

---

## 10. 建议的最小模板

```java
public class MyExpansion extends InfiniteExpansion {

  @Override
  public void onEnable() {
    // 1) load config
    // 2) register listeners
  }

  @Override
  public void onDisable() {
    // release resources
  }

  @Override public String getExpansionName() { return "MyExpansion"; }
  @Override public String getVersion() { return "1.0.0"; }
  @Override public String getAuthor() { return "You"; }
}
```
