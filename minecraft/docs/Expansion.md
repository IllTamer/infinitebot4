# Infinite-Bot-3 附属编写教程

## 前言

InfiniteBot-v3.+较一代完善了附属开发方面的不足，较二代补足了多服间数据互通的分布式需求。插件的附属拓展系统借鉴了Buukit-Plugin模式，由插件主体提供可热加载、具有完善监听、独特API的附属开发模式。

> 即：附属除无需配置plugin.yml外其他一切写法与bukkit-plugin类似！

## 附属主类注册

附属主类需继承抽象类 `InfiniteExpansion` 并重写方法

-   `onEnable` 附属加载时调用
-   `onDisable` 附属卸载时调用
-   `getExpansionName` 用于插件注册附属，不为空！

其他父类自带方法详见 [[IExpansion]](../src/main/java/com/illtamer/infinite/bot/minecraft/api/IExpansion.java)

示例代码

```java
 public class ExampleExpansion extends InfiniteExpansion {
     @Override
     public void onEnable() {
         //TODO
     }
 
     @Override
     public void onDisable() {
         //TODO
     }
 
     @Override
     @NotNull
     public String getExpansionName() {
         return "ExampleExpansion";
     }
 }
```

## 附属配置文件注册

IB3已预先为您封装好了配置文件实体类 `ExpansionConfig`，您的配置文件应在附属初始化时被注册。当插件加载附属配置文件时，会从附属jar中寻找对应URL，若找到加载到缓存并自动生成到 `plugins/InfiniteBot3/expansions/附属名称` 下 示例代码

```java
 public class ExampleExpansion extends InfiniteExpansion {
     private IExpansion instance;
     private ExpansionConfig configFile;
     @Override
     public void onEnable() {
         instance = this;
         configFile = new ExpansionConfig("config.yml", instance);
         // ...
     }
 }
```

ExpansionConfig 已封装常用方法保存/重载/获取yml文件，详见 [[ExpansionConfig]](../src/main/java/com/illtamer/infinite/bot/minecraft/expansion/ExpansionConfig.java)

## 语言文件

语言文件基于 ExpansionConfig 二次封装实现，其固定格式为 `{name}-{type}.yml` (如 `language-zh_CN.yml`)。
语言文件相关 API 详见类 [[Language]](../src/main/java/com/illtamer/infinite/bot/minecraft/expansion/Language.java)，且在不断完善中。

## QQ事件监听+注册

> 注：该示例中所用到的类全位于 `com.illtamer.infinite.bot.api.event` 包下(包括Bukkit的同名类)

您的监听类应实现`Listener`接口，监听的方法应标有`@EventHandler`注解，方法中事件参数选择`InfiniteBot3`事件

-   监听部分示例代码

```java
 public class ExampleListener implements Listener {
     // 可选优先级设置项 property 优先级高的方法将被优先调用
     @EventHandler
     public void onEvent(MessageEvent event) {
         // 引用发送者的消息发送一条内容为“收到”的消息
         event.reply("收到");
         // 为保证注入消息转发类的插件能够正常工作，当您在执行例如监听关键字的特定回复操作后，请务必取消时间避免消息被转发
         event.setCancelled(true);
     }
 }
```

-   注册监听部分示例代码

> 附属注册的所有事件均会在附属卸载后自动被注销

```java
 public class ExampleExpansion extends InfiniteExpansion {
     private IExpansion instance;
     @Override
     public void onEnable() {
         instance = this;
         // 注册 IB3 事件
         EventExecutor.registerListener(new ExampleListener(), instance);
         // 注册 Bukkit 事件
         EventExecutor.registerBukkitListener(new BukkitListener());
         // ...
     }
 }
```

## API

插件本体中设置有两种 API

### 一：公共 API

在 `com.illtamer.infinite.bot.api` 包下，其中

-   `com.illtamer.infinite.bot.api.event` 包内是支持的 QQ 事件类型

-   `com.illtamer.infinite.bot.api.message` 包内是封装的消息对象。

-   `com.illtamer.infinite.bot.api.entity` 包内是事件中产生的实体类

-   `com.illtamer.infinite.bot.api.handle` 包内封装了 go-cqhttp 的 WebAPI

    -   您可使用该包内的 `OpenAPIHandling` 快速调用相关 API

-   `com.illtamer.infinite.bot.api.util` 包内则是该模块使用到的工具类

### 二：Plugin API

作者所能确保的不会删减的 API 在 `com.illtamer.infinite.bot.minecraft.api` 包内，其中 [[StaticAPI]](../src/main/java/com/illtamer/infinite/bot/minecraft/api/StaticAPI.java) 提供部分 API 的静态方法调用。

插件的启动类为 `com.illtamer.infinite.bot.minecraft.Bootstrap`，如有需要，您可获取启动类实例以获取附属加载类 [[ExpansionLoader]](../src/main/java/com/illtamer/infinite/bot/minecraft/expansion/ExpansionLoader.java) 的实例，对插件的附属进行修改操作。