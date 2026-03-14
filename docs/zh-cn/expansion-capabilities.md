# 附属能力与可用 API

本页从"附属作者"的角度，列出 InfiniteBot4-minecraft 提供的核心能力与常用 API 入口。

---

## 1. 生命周期与基础能力

### 1.1 生命周期

内部附属：继承 `InfiniteExpansion`

- `onEnable()`：附属启用
- `onDisable()`：附属卸载

外部附属：继承 `AbstractExternalExpansion`

- 通过 `register(plugin)` 挂载到 InfiniteBot4
- InfiniteBot4 卸载或你的插件卸载时，会触发 `onDisable()`

### 1.2 唯一标识符

- `name-version::author`
- 由 `ExpansionUtil.formatIdentifier(...)` 生成
- `ExpansionLoader` 以它作为 key

---

## 2. 配置能力

### 2.1 ExpansionConfig（手动配置文件）

类：`com.illtamer.infinite.bot.minecraft.expansion.ExpansionConfig`

能力：

- 首次加载时若磁盘文件不存在：自动从 expansion jar 的资源释放到 `plugins/InfiniteBot4/expansions/<name>/` 下
- 支持 `save()` / `reload()` / `getConfig()`
- 支持配置版本迁移：当代码侧传入 version > 文件 version 时，自动备份旧文件并合并键值

典型用法：

```java
public class MyExpansion extends InfiniteExpansion {

  private ExpansionConfig config;

  @Override
  public void onEnable() {
    // 第三个参数为配置版本号，用于自动迁移
    config = new ExpansionConfig("config.yml", this, 1);
    String foo = config.getConfig().getString("foo", "bar");
  }

  @Override
  public void onDisable() {
    config.save();
  }

  @Override public String getExpansionName() { return "MyExpansion"; }
  @Override public String getVersion() { return "1.0.0"; }
  @Override public String getAuthor() { return "You"; }
}
```

> 注意：`config.yml` 必须作为资源文件打进 jar（位于 resources 根目录或同路径）。

### 2.2 AutoLoadConfiguration（自动配置类）

类：`com.illtamer.infinite.bot.minecraft.expansion.automation.AutoLoadConfiguration`

能力：

- 通过注解声明配置文件名与字段映射
- 自动加载/写回字段
- 通过 `Registration` 注册为单例，并在附属卸载时自动写回并反注册

关键点：

- 类上必须有 `@ConfigClass(name = "xxx.yml")`
- 字段可用 `@ConfigField(ref = "path", value = "默认值")`
- Enum 字段建议实现 `parse(String)/valueOf(String)` 之类静态转换方法（框架会尝试 `parse`、`getXxx`、`valueOf`）

---

## 3. 事件与监听能力

### 3.1 Perpetua（OneBot）事件监听

入口类：`com.illtamer.infinite.bot.minecraft.api.EventExecutor`

注册：

```java
EventExecutor.registerEvents(new MyListener(), this);
```

监听类：实现 `com.illtamer.infinite.bot.minecraft.api.event.Listener`，方法标注 `@EventHandler`：

```java
public class MyListener implements Listener {

  @EventHandler(priority = EventPriority.NORMAL)
  public void onAnyEvent(com.illtamer.perpetua.sdk.event.Event event) {
    // ...
  }
}
```

约束：

- 方法必须只有 1 个参数
- 参数类型必须是 `com.illtamer.perpetua.sdk.event.Event`（或其子类）
- 若事件实现了 `Cancellable`，且已取消，则后续监听会被跳过

### 3.2 注册 Bukkit 事件监听

```java
EventExecutor.registerBukkitEvent(new org.bukkit.event.Listener() { ... }, this);
```

卸载时框架会自动反注册。

---

## 4. StaticAPI（便捷入口）

类：`com.illtamer.infinite.bot.minecraft.api.StaticAPI`

常用能力：

- `StaticAPI.isAdmin(long qq)`：判断是否为管理员（来自 config.yml）
- `StaticAPI.inGroups(long groupId)`：判断群是否在监听列表
- `StaticAPI.isMaster()`：是否为主节点
- `StaticAPI.getRepository()`：获取 `PlayerDataRepository`
- `StaticAPI.getClient()`：获取 Perpetua Client 信息（如 appId、clientName）

---

## 5. 数据能力（玩家绑定/权限等）

接口：`PlayerDataRepository`

- YAML 模式：存储于 `plugins/InfiniteBot4/player_data.yml`，适合单服
- MySQL 模式：存储于表 `infinite_bot_3`（该表名沿用自 InfiniteBot3 时代，为保持数据兼容性未做更改），适合群组服共享

`PlayerData` 字段：

- `uuid`：离线/服务器侧 UUID（若业务使用）
- `validUUID`：Mojang 校验 UUID（若业务使用）
- `userId`：QQ 号
- `permission`：权限字符串列表（具体语义由你的附属定义）

> 注意：本体只提供"存取与缓存落盘"，具体绑定流程/命令/校验逻辑通常由业务附属实现。

---

## 6. 分布式能力（多服数据收集/广播）

类：`com.illtamer.infinite.bot.minecraft.api.distribute.DistributedEventProcessor<T>`

用途：

- 在多客户端（多服）场景下，向其它端广播事件并收集回调数据

基本用法示意：

```java
public class MyExpansion extends InfiniteExpansion {

  private DistributedEventProcessor<MyData> processor;

  @Override
  public void onEnable() {
    processor = new DistributedEventProcessor<>(this, MyData.class);
    processor.registerHandler("onlineCount", ctx -> {
      return new MyData(Bukkit.getOnlinePlayers().size());
    });

    // 监听广播与回调
    EventExecutor.registerEvents(processor.createListener(), this);
  }

  public void requestAll(Consumer<DistributedResult<MyData>> consumer) {
    processor.tryProcessEvent("onlineCount", new DistributedEventContext(), consumer, Throwable::printStackTrace);
  }
}
```

返回值：`DistributedResult<T>`

- `dataList`：各客户端回传数据 + 本地数据
- `failedClientList`：超时或解析失败的客户端列表

约束：

- `T` 必须能被 Gson 正确序列化/反序列化
- 默认等待超时 3 秒（源码内固定）

---

## 7. 资源能力（读取/释放 jar 内资源）

IExpansion 提供：

- `InputStream getResource(String name)`
- `void saveResource(String path, boolean replace)`

规范建议：

- 资源路径使用 `/`，并确保被打入 jar。
- 大文件/二进制资源释放建议放在异步线程，避免阻塞主线程。
