## Infinite Bot for Minecraft

## 使用

### 加载

- Plugin

  > **Notice**: 插件本体理论支持 热加载&热卸载，但不建议 **频繁的热部署插件**。插件附属管理为插件内部支持实现，其完全支持热部署，可放心食用。

  请您将构建的 `InfiniteBot-minecraft-all-3.0.0-SNAPSHOT.jar` 放入服务器的 `plugins` 文件夹内。
  待生成配置文件后，请参考 [[config.yml]](src/main/resources/config.yml) 内注释详细配置 go-cqhttp 的连接信息。


- Plugin Expansion

  遵守[附属开发规范](docs/Expansion.md)的插件附属应当被放置在 `plugins\InfiniteBot3\expansions` 路径内。若附属注册了相应的配置文件，则在附属被加载后与附属注册名称相同的配置文件夹也将在同级目录下生成。

### 指令

所有的指令与补全仅管理员(`OP`)可用

```
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

### 资源文件夹结构

```
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

### 使用

详见仓库 [infinite-bot-3-expansion](https://github.com/IllTamer/infinite-bot-3-expansion)

### 编写

出于可用性、简洁性、不重复造轮子等方面考虑，本人强烈建议您以下方的附属开发手册为参考编写插件拓展，以快速开发代替传统附属插件编写方式——再写一个Bukkit插件。

[Expansion-附属开发手册](docs/Expansion.md)