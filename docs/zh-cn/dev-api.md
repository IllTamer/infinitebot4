# Perpetua SDK / API 速查

本页用于帮助"插件开发者/附属开发者"快速定位 **Perpetua SDK** 与 InfiniteBot4-minecraft 的 API 入口。

> 重要说明：
>
> - InfiniteBot4-minecraft 仓库当前以 `:minecraft` 子模块为主（并通过依赖引入 Perpetua SDK）。
> - OneBot（原 go-cqhttp 生态）相关的连接、事件、Web API、广播等能力均由 **Perpetua SDK** 提供。
>
> 因此，本页不再维护旧版 `IB3 API`、`CQHttpWebSocketConfiguration`、`ib3-spring-boot-starter` 等历史内容。

---

## 1. 依赖引入（附属开发）

开发 Expansion 时，直接依赖 `minecraft` 模块即可（其中已包含 Perpetua SDK 依赖）。

依赖坐标与引入方式详见《附属（Expansion）开发指南》的"依赖引入"章节。

---

## 2. 事件模型

- Perpetua 事件基类：`com.illtamer.perpetua.sdk.event.Event`
- Bukkit 插件侧事件分发器：`com.illtamer.infinite.bot.minecraft.api.EventExecutor`

在 Expansion 中监听 Perpetua 事件：

```java
public class MyListener implements com.illtamer.infinite.bot.minecraft.api.event.Listener {

  @com.illtamer.infinite.bot.minecraft.api.event.EventHandler
  public void onEvent(com.illtamer.perpetua.sdk.event.Event event) {
    // ...
  }
}

// onEnable
EventExecutor.registerEvents(new MyListener(), this);
```

> 监听方法必须只有 1 个参数，且为 `Event`（或其子类）。

---

## 3. Web API / 广播 / 客户端列表

InfiniteBot4-minecraft 在连接建立后会设置：

- `OpenAPIHandling.setClientName(connection.name)`（在成功连接 Perpetua WebAPI 后设置客户端别名）

常用入口（均来自 Perpetua SDK）：

- `com.illtamer.perpetua.sdk.handler.OpenAPIHandling`
  - `getStatus()` / `getLoginInfo()`
  - `getClientList()`
  - `sendBroadcastData(...)` / `sendBroadcastDataCallback(...)`

插件侧的连接状态巡检：

- `StatusCheckRunner` 会周期性调用 `OpenAPIHandling.getStatus()` / `getLoginInfo()`，异常时触发 `StaticAPI.reconnected()`。

---

## 4. 分布式能力（多服/多端）

如果你希望在多客户端环境收集数据：

- 使用 `com.illtamer.infinite.bot.minecraft.api.distribute.DistributedEventProcessor<T>`

它基于 Perpetua 的 broadcast + callback 事件实现结果收集。

详见文档：

- 《附属能力与可用 API》 -> 分布式能力章节

---

## 5. 配置系统

插件侧提供两套：

1. `ExpansionConfig`：传统 yml 文件封装 + 版本迁移
2. `AutoLoadConfiguration`：注解驱动的字段映射 + 自动保存

详见：

- 《附属能力与可用 API》
- 《附属开发指南》

---

## 6. 额外提示

- InfiniteBot4 暂未计划支持任何"频道"类 API（以仓库 README/作者声明为准）。
- 若你要做更偏业务层的"命令解析/消息构建"，建议直接参考 Perpetua SDK 的实体类与 handler。
