# for Minecraft

基于 go-cqhttp 提供的 API 服务，IB3 天然支持群组服/分布式服务。

您需要做的仅仅是在 BC 端上加载插件，与需要互通的其它子端配置相同地连接参数与 mysql 服务。
接着，您可以在不同的子端上加载不同的附属：如登陆服加载 defence-manager 开启白名单验证码服务，在生存服加载 chat-manager 运行消息互通服务...

<del>（好叭，其实 BC 端正在适配中）</del>

## 环境

### Java 8

该版本可直接运行运行插件

### Java 9+

若您的服务器使用了 Java9 及以上的版本，则需在启动的批处理文件中加入以下 JVM 参数允许来自未命名模块的反射调用。

```cmd
--add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.lang.invoke=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED
```

## 部署

- 插件

  > **Notice**: 插件本体理论支持 热加载&热卸载，但不建议 **频繁的热部署插件**。插件附属管理为插件内部支持实现，其完全支持热部署，可放心食用。

  请您将构建的 `InfiniteBot-minecraft-all-3.0.0-SNAPSHOT.jar` 放入服务器的 `plugins` 文件夹内。
  待生成配置文件后，请参考 [[config.yml]](src/main/resources/config.yml) 内注释详细配置 go-cqhttp 的连接信息。

- 插件附属

  遵守[附属开发规范](docs/Expansion.md)的插件附属应当被放置在 `plugins\InfiniteBot3\expansions` 路径内。若附属注册了相应的配置文件，则在附属被加载后与附属注册名称相同的配置文件夹也将在同级目录下生成。

## 指令

所有的指令与补全仅管理员(`OP`)可用

```text
ib3:
 ├──help: 获取帮助
 ├──check: 检查账号的连接状态
 ├──reload: 重载配置文件
 ├──expansions
 │   ├──list: 列出所有加载的附属名称
 │   └──reload: 重载 expansions 目录下所有附属
 ├──load
 │   └──[附属文件名]: 加载名称对应附属
 └──unload
     └──[附属名称]: 卸载名称对应附属
```

## 资源文件夹结构

```text
InfiniteBot3
 ├──expansions: 附属及其配置文件夹
 │   ├──basic-manager-1.0.jar: 基础管理插件
 │   ├──BasicManager: 基础管理插件配置文件夹
 │   │   └──config.yml: 基础管理插件配置文件
 │   └──...
 ├──config.yml: 插件本体配置文件
 └──player_data.yml: 
```

## 附属相关

### 下载

详见仓库 [infinite-bot-3-expansion](https://github.com/IllTamer/infinite-bot-3-expansion)


