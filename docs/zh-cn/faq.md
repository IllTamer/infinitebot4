# 常见问题与排障（FAQ）

本页聚焦 InfiniteBot4（Minecraft 插件）常见部署/运行问题。

---

## 1. 插件启动后提示连接失败 / 一直重连

现象：

- 控制台出现"账号连接失败，请检查控制台输出处理，或等待自动重连"
- 或反复出现"检测到 WebSocket 连接断开，尝试重连 perpetua 中"

排查清单：

1. **确认 Perpetua 服务已启动**
   - InfiniteBot4 依赖 Perpetua（OneBot 连接管理）提供 WebSocket/WebAPI 服务。
2. **检查 `plugins/InfiniteBot4/config.yml` 的 `connection.webapi.host/port`（WebAPI 端口）**
   - host 建议使用 `127.0.0.1` 或 Perpetua 实际所在服务器 IP
   - port 必须填写 **Perpetua 的 WebAPI 端口**（WebSocket 端口由 Perpetua 在 WebAPI 连接后注册并用于事件分发，本插件侧通常无需单独配置）
3. **检查 `connection.authorization`**
   - 若 Perpetua/OneBot 开启了 token 鉴权，需要填入相同 token
   - 未启用则保持空字符串或 null
4. **防火墙/端口占用**
   - 确认 host/port 可从 Minecraft 服务器进程访问

---

## 2. `/ib4` 没反应 / 没有命令

1. 确认你输入的是：
   - `/ib4 help`
   - 或 `/InfiniteBot4 help`
2. 命令仅对 **OP** 生效（实现里 `sender.isOp()` 直接拦截）。
3. 确认 `plugin.yml` 中命令已注册（本插件已内置）。

---

## 3. 附属（Expansion）不加载 / 加载报错

### 3.1 内部附属 jar 放置位置

必须放在：

- `plugins/InfiniteBot4/expansions/*.jar`

### 3.2 主类要求

内部附属主类必须：

- `extends com.illtamer.infinite.bot.minecraft.expansion.manager.InfiniteExpansion`
- 拥有 public 无参构造（默认即可）

若你是外部附属：

- 需要 `extends AbstractExternalExpansion` 并在你的 Bukkit 插件 `onEnable()` 主动 `register(this)`

### 3.3 唯一标识符冲突

ExpansionLoader 以 `name-version::author` 作为 key。

- 若重复（同标识符出现多次），会导致覆盖/加载异常。

---

## 4. 修改数据库配置后不生效

`database.type` 与 mysql 连接参数属于"启动期读取"。

- 修改后需要**重启服务器或重启插件**（仅 `reload` 不会重建 repository 与 datasource）。

---

## 5. Java 9+ 反射相关报错

当前插件在以下位置存在反射：

- `BotNettyHolder` 通过 `MethodHandles.Lookup.IMPL_LOOKUP` 调用 `ThreadPoolExecutor#interruptWorkers`（用于连接线程中断）

若你在高版本 Java 上遇到非法反射/访问错误，可尝试在启动参数中加入（按需）：

```bash
--add-opens=java.base/java.lang.reflect=ALL-UNNAMED \
--add-opens=java.base/java.lang.invoke=ALL-UNNAMED \
--add-opens=java.base/java.util=ALL-UNNAMED
```

---

## 6. 需要更多帮助

- 交流群：`QQ: 863522624`
- GitHub：<https://github.com/IllTamer/infinitebot4>
