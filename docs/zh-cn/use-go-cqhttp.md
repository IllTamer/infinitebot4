# Install go-cqhttp

<big>Infinite Bot v3 摒弃了以往将 Mirai 嵌入程序主体的做法，转而使用高性能机器人 go-cqhttp 所提供的 websocket 与 http 服务。因此，您在使用本程序前应该先安装开启 go-cqhttp 服务。</big>

## 二进制安装

其详细步骤如下：

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

3. 配置生成的 `config.yml` 中的 `account` 与 `servers` 节点。
   
   如果您将 http 服务开放到公网，建议您额外配置 `default-middlewares.access-token` 字段，并将该 token 设置到 `plugins\InfiniteBot3\config.yml` 的 `connection.authorization` 字段，以对暴露在公网的 go-cqhttp 服务提供保护。
   > Notice：若您将 http/websocket 服务开放到公网，供 InfiniteBot 远程访问，请不要忘记开放对应端口。

4. 运行可执行文件，开启 go-cqhttp 服务。
   
   若您先前使用过 InfiniteBot 系列插件或其它机器人插件，可将账号登录信息直接迁移，并在 go-cqhttp 配置中使用账号密码密码登录，可大大降低被风控风险。
   
   > 迁移方式
   > - InfiniteBot 系列插件：将服务器主文件夹下或 `plugins\InfiniteBot2` 文件夹下的 `InfiniteBotInfo.json` 文件复制并重命名为 `device.json`，拷贝到 go-cqhttp 目录下
   > - 其它机器人插件：将名为 `device.json` 与 `session.token` 的文件拷贝到 go-cqhttp 目录下
   
   账号成功登录后，您将在控制台看见以下输出

   ```
   [2022-08-09 19:52:05] [INFO]: 登录成功 欢迎使用: IllTamer
   [2022-08-09 19:52:05] [INFO]: 开始加载好友列表...
   [2022-08-09 19:52:05] [INFO]: 共加载 672 个好友.
   [2022-08-09 19:52:05] [INFO]: 开始加载群列表...
   [2022-08-09 19:52:13] [INFO]: 共加载 99 个群.
   [2022-08-09 19:52:13] [INFO]: 资源初始化完成, 开始处理信息.
   [2022-08-09 19:52:13] [INFO]: アトリは、高性能ですから!
   [2022-08-09 19:52:13] [INFO]: CQ HTTP 服务器已启动: [::]:5700
   [2022-08-09 19:52:13] [INFO]: 正在检查更新.
   [2022-08-09 19:52:13] [INFO]: CQ WebSocket 服务器已启动: [::]:8080
   [2022-08-09 19:52:14] [INFO]: 检查更新完成. 当前已运行最新版本.
   [2022-08-09 19:52:14] [INFO]: 开始诊断网络情况
   ```

## Docker

<del>正在等待好心人 pr 中 ...</del>

## 其它

   - [尝试解决风控措施](https://github.com/Mrs4s/go-cqhttp/issues/1320)
