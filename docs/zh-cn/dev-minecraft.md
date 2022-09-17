# Plugin - Expansion

## 导入

在开发 `InfiniteBot3-minecraft` 之前，您需要向您的项目中引入插件的坐标（或直接 [[下载插件]](https://github.com/IllTamer/infinitebot3/releases) 并导入，不推荐）。

> 在 `minecraft` 模块中已完整包含 `api` 模块，所有在上文介绍过的 API 均被允许使用。

### Maven

```xml
<dependency>
  <groupId>com.illtamer.infinite.bot</groupId>
  <artifactId>minecraft</artifactId>
  <version>3.1.0</version>
</dependency>
```

## 编写附属

!> 出于可用性、简洁性、不重复造轮子等方面考虑，本人强烈建议您以下方的附属开发规范为参考编写插件拓展，以快速开发代替传统附属插件编写方式——再写一个Bukkit插件<del>（如果你喜欢的话）</del>。

### 前言

InfiniteBot-v3.+较一代完善了附属开发方面的不足，较二代补足了多服间数据互通的分布式需求。插件的附属拓展系统借鉴了Buukit-Plugin模式，由插件主体提供可热加载、具有完善监听、独特API的附属开发模式。

> 即：附属除无需配置 plugin.yml 外生命周期相关 API 向 bukkit-plugin 规范靠齐。

### 附属主类注册

#### Standard

附属主类需继承抽象类 `InfiniteExpansion` 并重写方法

-   `onEnable` 附属加载时调用
-   `onDisable` 附属卸载时调用
-   `getExpansionName` 用于注册附属，不为空！
-   `getVersion` 用于注册附属，不为空！
-   `getAuthor` 用于注册附属，不为空！

其他父类自带方法详见 [[IExpansion]](../src/main/java/com/illtamer/infinite/bot/minecraft/api/IExpansion.java)

> `expansionName`, `version` 与 `author` 共同组成了附属唯一性标识 `expansionName-version::author`。\
> 一个唯一性标识最多允许注册一个

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
    @Override
    @NotNull
    public String getVersion() {
        return "1.0";
    }
    @Override
    @NotNull
    public String getAuthor() {
        return "IllTamer";
    }
 }
```

#### With a Plugin

若您希望您的插件能与 `InfiniteBot3-minecraft` 挂钩(hook)，可以按以下形式在您的插件中新建一个附属类并将其注册为外部附属(External Expansion)

编写附属类

```java
public class ExternalExpansion extends AbstractExternalExpansion {
    @Override
    public void onEnable() {
        // TODO
    }
    @Override
    public void onDisable() {
        // TODO
    }
    @Override
    public String getExpansionName() {
        return "ExternalExpansion";
    }
    @Override
    public String getVersion() {
        return "1.0-SNAPSHOT";
    }
    @Override
    public String getAuthor() {
        return "IllTamer";
    }
}
```

注册外部附属

```java
public class TestPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        new ExternalExpansion().register(this);
    }
}
```

### 附属配置文件注册

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

### 语言文件

语言文件基于 ExpansionConfig 二次封装实现，其固定格式为 `{name}-{type}.yml` (如 `language-zh_CN.yml`)。
语言文件相关 API 详见类 [[Language]](../src/main/java/com/illtamer/infinite/bot/minecraft/expansion/Language.java)，且在不断完善中。

### QQ事件监听+注册

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

插件本体中内含两种 API

1. 模块 API

   见 [开发相关 - API](dev-api.md)

2. 插件 API

   作者所能确保的不会删减的 API 在 `com.illtamer.infinite.bot.minecraft.api` 包内，其中 [[StaticAPI]](../src/main/java/com/illtamer/infinite/bot/minecraft/api/StaticAPI.java) 提供部分 API 的便捷静态方法调用。

   插件的启动类为 `com.illtamer.infinite.bot.minecraft.Bootstrap`，如有需要，您可获取启动类实例以获取附属加载类 [[ExpansionLoader]](../src/main/java/com/illtamer/infinite/bot/minecraft/expansion/ExpansionLoader.java) 的实例，对插件的附属进行修改操作。
