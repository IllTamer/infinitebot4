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

?> _TODO_ 完善示例

### Message

### Event Channel

### Web API

### Util