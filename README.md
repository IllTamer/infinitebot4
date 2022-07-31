# Infinite Bot v3

```
 .___        _____.__       .__  __        __________        __    ________
 |   | _____/ ____\__| ____ |__|/  |_  ____\______   \ _____/  |_  \_____  \
 |   |/    \   __\|  |/    \|  \   __\/ __ \|    |  _//  _ \   __\   _(__  <
 |   |   |  \  |  |  |   |  \  ||  | \  ___/|    |   (  <_> )  |    /       \
 |___|___|  /__|  |__|___|  /__||__|  \___  >______  /\____/|__|   /______  /
          \/              \/              \/       \/                     \/
```

## 声明

- 若您在使用时有任何疑问，欢迎入群讨论咨询 QQ: 863522624

- 若您为 Minecraft 公益服主且服务器资源难以承受 go-cqhttp 的运行，欢迎 [[联系我]](https://api.vvhan.com/api/qqCard?qq=765743073) 。我与我的云服务很乐意为您提供一份力所能及的援助。

## 前置安装

Infinite Bot v3 摒弃了以往将 Mirai 嵌入程序主体的做法，转而使用高性能机器人 go-cqhttp 所提供的 websocket 与 http 服务。因此，您在使用本程序前应该先安装开启 go-cqhttp 服务。其详细步骤如下：

1. 获取 go-cqhttp 的最新 [[release]](https://github.com/Mrs4s/go-cqhttp/releases)

2. 运行压缩包中的可执行文件，生成 `http` + `正向websocket` 配置文件 `config.yml`
```text
未找到配置文件，正在为您生成配置文件中！
请选择你需要的通信方式:
> 0: HTTP通信
> 1: 云函数服务
> 2: 正向 Websocket 通信
> 3: 反向 Websocket 通信
请输入你需要的编号(0-9)，可输入多个，同一编号也可输入多个(如: 233)
您的选择是: 0 2
```

3. 配置 `config.yml` 的 `account` 与 `servers` 节点，如果您将 http 服务开放到公网，建议您额外配置 `default-middlewares.access-token` 字段。
    > Notice：若您将 http/websocket 服务开放到公网，供 InfiniteBot 远程访问，请不要忘记开放对应端口。

4. 运行可执行文件，开启 go-cqhttp 服务。

## Bukkit 插件安装

更多信息请查看 [[minecraft]](/minecraft)

## 开发

### InfiniteBot Expansion

更多信息请查看 [[minecraft]](/minecraft)

### Maven

InfiniteBot-v3 为支持 go-cqhttp 实现了一系列包括事件监听、消息回调、end-point 快速操作等 API
。您可将 [[api]](/api) 模块作为调用 go-cqhttp 的前置依赖库导入项目，进行开发。

```xml
<dependency>
    <groupId>com.illtamer.infinite.bot</groupId>
    <artifactId>api</artifactId>
    <version>3.0.0-SNAPSHOT</version>
</dependency>
```

毫无疑问，IB3 需要您提供您开启的 go-cqhttp 服务的详细参数以便于与机器人建立连接。请在程序中调用以下函数以便于初始化连接。 

```
CQHttpWebSocketConfiguration.start(httpUri, wsUri, authorization, eventConsumer);
```

该方法最后一个参数为事件基类 [Event](/api/src/main/java/com/illtamer/infinite/bot/api/event/Event.java) 的 `Consumer` 对象，即事件的处理函数。
在 Spring 框架中，您可以调用 `ApplicationEventPublisher#publish(Object)` 将事件托管。

### SpringBoot

InfiniteBot-v3 额外优化了在 SpringBoot 框架下的开发体验。您只需要进行以下操作即可使用 InfiniteBot

1. 导入 ib3-spring-boot-starter
```xml
<dependency>
    <groupId>com.illtamer.infinite.bot</groupId>
    <artifactId>ib3-spring-boot-starter</artifactId>
    <version>3.0.0-SNAPSHOT</version>
</dependency>
```

2. 在`application.yml`填写以下配置节点
```yaml
bot:
  http-uri: ''
  ws-uri: ''
  authorization: ''
```

## 致谢

- 感谢 polar、小豆子、1758983508 等腐竹在测试、策划方面提供的帮助与支持

- 感谢机器人插件的先驱者 [@Albert](https://github.com/mcdoeswhat)