package com.illtamer.infinite.bot.minecraft.api.distribute;

import com.google.gson.Gson;
import com.illtamer.infinite.bot.minecraft.api.IExpansion;
import com.illtamer.infinite.bot.minecraft.api.StaticAPI;
import com.illtamer.infinite.bot.minecraft.api.event.EventHandler;
import com.illtamer.infinite.bot.minecraft.api.event.Listener;
import com.illtamer.infinite.bot.minecraft.pojo.TimedBlockingCache;
import com.illtamer.infinite.bot.minecraft.util.StringUtil;
import com.illtamer.perpetua.sdk.Pair;
import com.illtamer.perpetua.sdk.entity.transfer.entity.Client;
import com.illtamer.perpetua.sdk.event.distributed.ClientBroadcastCallbackEvent;
import com.illtamer.perpetua.sdk.event.distributed.ClientBroadcastEvent;
import com.illtamer.perpetua.sdk.handler.OpenAPIHandling;
import com.illtamer.perpetua.sdk.util.Assert;
import lombok.NonNull;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分布式事件处理器
 * 提供统一的分布式事件分发和回调处理机制
 * @param <T> 事件数据类型
 * @apiNote <pre>
 *      广播事件的数据=payload
 *      payload=json(dataKey, context)
 *      dataKey=expansionName#eventKey#source.AppId#target.AppId
 *
 *      eventKey - 事件处理器唯一标识
 *
 *      cacheKey - 用于阻塞队列获取其他客户端数据
 *      cacheKey=AppId#expansionName#eventKey
 * </pre>
 */
public class DistributedEventProcessor<T> {

    private final TimedBlockingCache<String, String> blockingCache = new TimedBlockingCache<>(64);
    private final Map<String, Function<DistributedEventContext, T>> eventHandlers = new HashMap<>();
    private final String expansionName;
    private final Logger logger;
    private final Gson gson = new Gson();
    private final Class<T> dataType;

    /**
     * 分布式事件处理器构造器
     * @param expansion 插件实例
     * @param dataType 事件数据类型
     */
    public DistributedEventProcessor(IExpansion expansion, Class<T> dataType) {
        this.expansionName = expansion.getExpansionName();
        this.logger = expansion.getLogger();
        this.dataType = dataType;
    }

    /**
     * 注册事件处理器
     * @param eventKey 事件唯一标识
     * @param handler 事件处理函数
     */
    public void registerHandler(@NonNull String eventKey, @NonNull Function<DistributedEventContext, T> handler) {
        eventHandlers.put(eventKey, handler);
    }

    /**
     * 创建广播监听器
     * @return 监听器实例
     */
    public DistributedEventListener createListener() {
        return new DistributedEventListener();
    }

    /**
     * 尝试处理分布式事件
     * @param eventKey 事件标识
     * @param context 事件上下文
     * @param resultConsumer 结果消费器
     * @param errorConsumer 错误消费器
     */
    public void tryProcessEvent(@NonNull String eventKey, DistributedEventContext context,
                                @NonNull Consumer<DistributedResult<T>> resultConsumer,
                                Consumer<Exception> errorConsumer) {
        Function<DistributedEventContext, T> handler = eventHandlers.get(eventKey);
        Assert.notNull(handler, "No handler connect eventKey: %s", eventKey);

        Client client = StaticAPI.getClient();
        List<Client> clientList = OpenAPIHandling.getClientList();

        // 检查是否为单机模式
        if (isStandaloneMode(client, clientList)) {
            // 单机模式，直接执行
            T localData = handler.apply(context);
            DistributedResult<T> result = new DistributedResult<>();
            result.setDataList(Collections.singletonList(localData));
            result.setFailedClientList(Collections.emptyList());
            resultConsumer.accept(result);
            return;
        }

        // 分布式模式
        try {
            Pair<List<T>, List<Client>> pair = collectClientData(eventKey, context, client, clientList);
            List<T> dataList = pair.getKey();
            // 执行本地处理
            dataList.add(handler.apply(context));
            
            DistributedResult<T> result = new DistributedResult<>();
            result.setDataList(dataList);
            result.setFailedClientList(pair.getValue());
            resultConsumer.accept(result);
        } catch (Exception e) {
            logger.error("分布式事件处理异常: {}", eventKey, e);
            if (errorConsumer != null) {
                errorConsumer.accept(e);
            }
        }
    }

    /**
     * 创建广播监听器
     */
    public class DistributedEventListener implements Listener {

        /**
         * 接收广播事件
         */
        @EventHandler
        public void onClientBroadcast(ClientBroadcastEvent event) {
            BroadcastPayload payload = parsePayload(event.getData());
            // dataKey=expansionName#eventKey#source.AppId#target.AppId
            String dataKey = payload.dataKey;
            String eventKey = dataKey.split("#")[1];
            Client sourceClient = event.getClient();

            Function<DistributedEventContext, T> handler = eventHandlers.get(eventKey);
            if (handler == null) {
                OpenAPIHandling.sendBroadcastDataCallback(payload(dataKey, payload.context, null), event.getUuid(), sourceClient);
                return; // 未注册此事件处理器, 返回空响应
            }

//            // 验证是否为发送给自己的广播 no need
//            Client targetClient = StaticAPI.getClient();
//            if (!isValidBroadcast(dataKey, sourceClient, targetClient)) {
//                return;
//            }

            // 执行事件处理并返回结果
            T result = handler.apply(payload.context);
            String json = gson.toJson(result);
            OpenAPIHandling.sendBroadcastDataCallback(payload(dataKey, payload.context, json), event.getUuid(), sourceClient);
        }

        /**
         * 接收回调事件
         */
        @EventHandler
        public void onClientBroadcastCallback(ClientBroadcastCallbackEvent event) {
            BroadcastPayload payload = parsePayload(event.getData());
            String cacheKey = buildCacheKey(event.getClient(), payload.dataKey.split("#")[1]);
            // 未注册此事件的处理器返回的空响应，也需要被放入，避免被误判为超时
            blockingCache.put(cacheKey, payload.resp == null ? "" : payload.resp);
        }
    }

    /**
     * 收集其他客户端数据
     */
    private Pair<List<T>, List<Client>> collectClientData(String eventKey, DistributedEventContext context,
                                                          Client client, List<Client> clientList) {
        List<Client> targetClientList = clientList.stream()
                .filter(c -> !c.getAppId().equals(client.getAppId()))
                .collect(Collectors.toList());

        List<Client> failedClientList = new ArrayList<>();
        List<T> dataList = new ArrayList<>();

        if (targetClientList.isEmpty()) {
            return new Pair<>(dataList, failedClientList);
        }

        // 发送广播到所有其他客户端
        for (Client targetClient : targetClientList) {
            String dataKey = buildDataKey(client, targetClient, eventKey);
            OpenAPIHandling.sendBroadcastData(payload(dataKey, context, null), targetClient);
        }

        // 收集回调数据
        for (Client targetClient : targetClientList) {
            try {
                String cacheKey = buildCacheKey(targetClient, eventKey);
                String json = blockingCache.get(cacheKey, 3, TimeUnit.SECONDS);
                if (StringUtil.isBlank(json)) {
                    continue;
                }
                T data = gson.fromJson(json, dataType);
                dataList.add(data);
            } catch (InterruptedException | TimeoutException e) {
                logger.error("获取客户端 {} 数据超时", targetClient, e);
                failedClientList.add(targetClient);
            } catch (Exception e) {
                logger.error("解析客户端 {} 数据失败", targetClient, e);
                failedClientList.add(targetClient);
            }
        }

        return new Pair<>(dataList, failedClientList);
    }

    /**
     * 检查是否为单机模式
     */
    private boolean isStandaloneMode(Client client, List<Client> clientList) {
        return clientList.isEmpty() || clientList.stream()
                .allMatch(c -> c.getAppId().equals(client.getAppId()));
    }

    /**
     * 检查广播是否有效（非自身消息
     * @deprecated perp已确保不会发给自己 不需要
     */
    private boolean isValidBroadcast(String dataKey, Client source, Client target) {
        return dataKey.endsWith(String.format("%s#%s", source.getAppId(), target.getAppId()));
    }

    private String payload(String dataKey, DistributedEventContext context, String respJson) {
        return gson.toJson(new BroadcastPayload(dataKey, context, respJson));
    }

    private BroadcastPayload parsePayload(String payload) {
        return gson.fromJson(payload, BroadcastPayload.class);
    }

    private String buildDataKey(Client source, Client target, String eventKey) {
        return String.join("#", expansionName, eventKey, source.getAppId(), target.getAppId());
    }

    private String buildCacheKey(Client client, String eventKey) {
        return String.join("#", client.getAppId(), expansionName, eventKey);
    }

    /**
     * 广播数据负载
     */
    private static class BroadcastPayload {
        private final String dataKey;
        private final DistributedEventContext context;
        private final String resp;

        public BroadcastPayload(String dataKey, DistributedEventContext context, String resp) {
            this.dataKey = dataKey;
            this.context = context;
            this.resp = resp;
        }
    }

}