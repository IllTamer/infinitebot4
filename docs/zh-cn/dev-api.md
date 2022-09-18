# API

## 导入

### Maven

InfiniteBot-v3 为支持 go-cqhttp 实现了一系列包括事件监听、消息回调、end-point 快速操作等 API
。您可将 [[api]](/api) 模块作为调用 go-cqhttp 的前置依赖库导入项目，进行开发。

```xml
<dependency>
   <groupId>com.illtamer.infinite.bot</groupId>
   <artifactId>api</artifactId>
   <version>1.0.5</version>
</dependency>
```

毫无疑问，IB3 需要您提供您开启的 go-cqhttp 服务的详细参数以便于与机器人建立连接。请在程序中调用以下函数以便于初始化连接。 

```java
CQHttpWebSocketConfiguration.start(httpUri, wsUri, authorization, eventConsumer);
```

该方法最后一个参数为事件基类 [Event](/api/src/main/java/com/illtamer/infinite/bot/api/event/Event.java) 的 `Consumer<? exteds Event>` 对象，即事件的处理函数。

> 在 Spring 框架中，您可以调用 `ApplicationEventPublisher#publish(Object)` 将事件托管。


### SpringBoot

InfiniteBot-v3 额外优化了在 SpringBoot 框架下的开发体验。您只需要进行相应配置即可使用

1. 导入 ib3-spring-boot-starter

   ```xml
   <dependency>
      <groupId>com.illtamer.infinite.bot</groupId>
      <artifactId>ib3-spring-boot-starter</artifactId>
      <version>1.0.5</version>
   </dependency>
   ```

2. 在`application.yml`填写以下配置节点

   ```yaml
   bot:
     http-uri: ''
     ws-uri: ''
     authorization: ''
   ```

## 示例

!> InfiniteBot3 暂未计划支持任何频道 API

### Message

go-cqhttp 同时支持使用两类消息 —— CQ码与Json，故 InfiniteBot3 也分别支持两种消息的构建。两类消息分别对应实体类 `CQMessage` 与 `JsonMessage`，他们都有一个共同的抽象父类 `Message` 作为类型声明。

> 在与 go-cqhttp 的 WebSocket 长连接中，事件中的消息以CQ码的形式被传递与解析。一般的，我们使用Json这种层次分明的数据结构来构建需要发送的消息对象。

#### 生成

您可以使用 `MessageBuilder` 建造者工具类来生成一个 `Message` 实例：

```java
Message message = MessageBuilder.json()
         .text("Hello World")
         .build();
```

#### Message Chain

在 `Message` 被构造的过程中，其内部还会维护一个 `MessageChain` 对象来描述消息中各组成的类型 `TransferEntity`。[点击查看]((https://github.com/IllTamer/infinitebot3/blob/main/api/src/main/java/com/illtamer/infinite/bot/api/entity/transfer/))支持的类型

### Event Channel

?> _TODO_ 事件管道相关 API 正在开发中 ~

### Web API

api 模块已内置部分较为常用的 go-cqhttp Web API，您可通过 [[OpenAPIHandling]](https://github.com/IllTamer/infinitebot3/blob/main/api/src/main/java/com/illtamer/infinite/bot/api/handler/OpenAPIHandling.java) 便捷调用所有已支持的 API，或查看其内部实现。

#### 自定义 APIHandler

1. 继承 [[AbstractAPIHandler<T>]](https://github.com/IllTamer/infinitebot3/blob/main/api/src/main/java/com/illtamer/infinite/bot/api/handler/AbstractAPIHandler.java)

2. 赋予泛型正确的类型（go-cqhttp Http 接口的返回数据类型）

3. 向父类构造器中传入正确的 `endpoint` （相关信息请见 [请求说明](https://docs.go-cqhttp.org/api/#%E8%AF%B7%E6%B1%82%E8%AF%B4%E6%98%8E)）

4. 将 API 所需参数作为成员变量声明并赋值

最终您实现的 APIHandler 应类似以下格式，使用 `APIHandler#request` 方法调用相关 Web API

```java
/**
 * 获取陌生人信息
 * */
@Getter
public class StrangerGetHandler extends AbstractAPIHandler<Map<String, Object>> {
    /**
     * QQ 号
     * */
    @SerializedName("user_id")
    private Long userId;
    /**
     * 是否不使用缓存（使用缓存可能更新不及时, 但响应更快）
     * */
    @SerializedName("no_cache")
    private Boolean noCache;

    public StrangerGetHandler() {
        super("/get_stranger_info");
    }

    public StrangerGetHandler setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public StrangerGetHandler setNoCache(Boolean noCache) {
        this.noCache = noCache;
        return this;
    }

    @NotNull
    public static Stranger parse(@NotNull Response<Map<String, Object>> response) {
        final Gson gson = new Gson();
        return gson.fromJson(gson.toJson(response.getData()), Stranger.class);
    }
}
```

### Util

- AdapterUtil 
   
   go-cqhttp 兼容性工具类

- Assert

   断言工具类

- ClassUtil

   反射工具类

- HttpRequestUtil

   http 工具类

- Maps

   低版本 `Map#of` 实现