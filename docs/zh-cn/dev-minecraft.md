# 附属（Expansion）开发指南

本页面向"插件开发者/附属作者"，介绍 InfiniteBot4-minecraft 的 Expansion 体系：内部附属（jar 热加载）与外部附属（hook 注册）。

---

## 1. 依赖引入

开发 Expansion 时，依赖 `minecraft` 模块即可（其已引入 Perpetua SDK）：

### Maven

```xml
<dependency>
  <groupId>com.illtamer.infinite.bot</groupId>
  <artifactId>minecraft</artifactId>
  <version>{version}</version>
  <scope>provided</scope>
</dependency>
```

### Gradle

```groovy
dependencies {
  compileOnly 'com.illtamer.infinite.bot:minecraft:{version}'
}
```

> 版本号以你实际使用的发布版本为准。

## 2. 编写附属（内部附属 / 外部附属）

建议优先使用 Expansion 体系来扩展业务，而不是再写一个"完整 Bukkit 插件"。

InfiniteBot4 的 Expansion 借鉴 Bukkit Plugin 生命周期：

- 有 `onEnable/onDisable`
- 有独立的配置目录
- 有统一的事件分发与注销逻辑
- 内部附属支持热加载/卸载（独立类加载器）

---

### 2.1 内部附属（Embedded Expansion）

内部附属以 jar 形式放入：

- `plugins/InfiniteBot4/expansions/*.jar`

主类要求：

- `extends com.illtamer.infinite.bot.minecraft.expansion.manager.InfiniteExpansion`
- 实现并返回非空：`getExpansionName/getVersion/getAuthor`

示例：

```java
import com.illtamer.infinite.bot.minecraft.expansion.manager.InfiniteExpansion;
import org.jetbrains.annotations.NotNull;

public class ExampleExpansion extends InfiniteExpansion {

  @Override
  public void onEnable() {
    getLogger().info("enabled");
  }

  @Override
  public void onDisable() {
    getLogger().info("disabled");
  }

  @Override @NotNull
  public String getExpansionName() { return "ExampleExpansion"; }

  @Override @NotNull
  public String getVersion() { return "1.0.0"; }

  @Override @NotNull
  public String getAuthor() { return "You"; }
}
```

标识符：

- `name-version::author`

> 规范建议：name 不要包含 `-`。

---

### 2.2 外部附属（External Expansion / Hook）

当你已有 Bukkit 插件，希望与 InfiniteBot4 挂钩：

1. 编写外部附属类：继承 `AbstractExternalExpansion`
2. 在你的插件 `onEnable()` 中 `register(this)`
3. 在你的插件 `onDisable()` 中（推荐）主动 `unregister()`

示例：

```java
import com.illtamer.infinite.bot.minecraft.expansion.manager.AbstractExternalExpansion;

public class ExternalExpansion extends AbstractExternalExpansion {
  @Override public void onEnable() {}
  @Override public void onDisable() {}

  @Override public String getExpansionName() { return "ExternalExpansion"; }
  @Override public String getVersion() { return "1.0.0"; }
  @Override public String getAuthor() { return "You"; }
}
```

```java
public class YourPlugin extends JavaPlugin {

  private ExternalExpansion expansion;

  @Override
  public void onEnable() {
    expansion = new ExternalExpansion();
    expansion.register(this);
  }

  @Override
  public void onDisable() {
    if (expansion != null && expansion.isRegister()) {
      expansion.unregister();
    }
  }
}
```

> 如果你忘记注销，InfiniteBot4 会在 `PluginDisableEvent` 中被动注销，但会输出警告日志。

---

## 3. 配置文件

### 3.1 ExpansionConfig（手动 yml）

```java
import com.illtamer.infinite.bot.minecraft.expansion.ExpansionConfig;

public class ExampleExpansion extends InfiniteExpansion {

  private ExpansionConfig config;

  @Override
  public void onEnable() {
    config = new ExpansionConfig("config.yml", this, 1); // 第三个参数为配置版本号，用于自动迁移
    String foo = config.getConfig().getString("foo", "bar");
  }
}
```

- 若磁盘不存在，会从 jar 资源释放
- 支持配置版本迁移（生成 `.bak`）

### 3.2 AutoLoadConfiguration（自动配置类）

- 适合字段多、希望减少样板代码的场景
- 附属卸载时会自动写回并反注册

详见：

- 《附属能力与可用 API》

---

## 4. 事件监听与注册

### 4.1 监听 Perpetua 事件

```java
import com.illtamer.infinite.bot.minecraft.api.EventExecutor;
import com.illtamer.infinite.bot.minecraft.api.event.EventHandler;
import com.illtamer.infinite.bot.minecraft.api.event.Listener;
import com.illtamer.perpetua.sdk.event.Event;

public class ExampleListener implements Listener {
  @EventHandler
  public void onEvent(Event event) {
    // ...
  }
}

// onEnable
EventExecutor.registerEvents(new ExampleListener(), this);
```

### 4.2 注册 Bukkit 事件

```java
EventExecutor.registerBukkitEvent(new org.bukkit.event.Listener() {
  // ...
}, this);
```

框架会在卸载时自动注销。

---

## 5. 分布式能力（可选）

- 使用 `DistributedEventProcessor<T>`
- 适用于多服/多端的数据收集与协作

详见：

- 《附属能力与可用 API》 -> 分布式能力章节

## 6. API 入口

- Perpetua SDK：OneBot 连接、事件、Web API、广播等能力
- InfiniteBot4-minecraft API（尽量稳定）：`com.illtamer.infinite.bot.minecraft.api`
  - `StaticAPI`：常用静态入口（管理员/群过滤、repository、client、master 等）
  - `EventExecutor`：事件注册/注销、Bukkit 事件注册
  - `BotScheduler`：线程池与定时任务

如果你需要对附属进行加载/卸载操作：

- `BukkitBootstrap.getInstance().getExpansionLoader()`

> 注意：`ExpansionLoader` 属于较底层能力，业务附属一般不建议直接操作其它附属。
