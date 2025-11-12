package com.illtamer.infinite.bot.minecraft.configuration;

import com.illtamer.infinite.bot.minecraft.api.BotScheduler;
import com.illtamer.infinite.bot.minecraft.api.StaticAPI;
import com.illtamer.infinite.bot.minecraft.configuration.config.BotConfiguration;
import com.illtamer.perpetua.sdk.event.Event;
import com.illtamer.perpetua.sdk.handler.OpenAPIHandling;
import com.illtamer.perpetua.sdk.websocket.OneBotConnection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class BotNettyHolder {

    protected ThreadPoolExecutor websocketExecutor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1));

    private final Consumer<ThreadPoolExecutor> interruptConsumer;
    private final Logger logger;
    private final Consumer<Event> eventConsumer;

    public BotNettyHolder(Logger logger, Consumer<Event> eventConsumer) {
        this.logger = logger;
        this.eventConsumer = eventConsumer;
    }

    /**
     * 开启 WebSocket 连接
     * */
    public void connect() {
        interruptConsumer.accept(websocketExecutor);
        final BotConfiguration.ConnectionConfig connection = BotConfiguration.connection;
        websocketExecutor.execute(new WebSocketRunner(connection));
        BotScheduler.runTaskLater(() -> {
            OpenAPIHandling.setClientName(connection.name);
            StaticAPI.getClient().setClientName(connection.name);
        }, 3L);
    }

    public void checkConnection() {
        if (OneBotConnection.isRunning()) {
            logger.info("账号连接成功");
        } else {
            logger.warning("账号连接失败，请检查控制台输出处理，或等待自动重连");
        }
    }

    public void close() {
        try {
            websocketExecutor.shutdown();
            logger.info("WebSocket 连接关闭 " + (websocketExecutor.isShutdown() ? "成功" : "失败"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    {
        Consumer<ThreadPoolExecutor> consumer;
        try {
            final Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            field.setAccessible(true);
            MethodHandles.Lookup IMPL_LOOKUP = (MethodHandles.Lookup) field.get(null);
            final MethodHandle methodHandle = IMPL_LOOKUP
                    .findVirtual(ThreadPoolExecutor.class, "interruptWorkers", MethodType.methodType(void.class));
            consumer = object -> {
                try {
                    methodHandle.invoke(object);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            };
        } catch (Exception e) {
            consumer = object -> {}; // do nothing
            e.printStackTrace();
        }
        this.interruptConsumer = consumer;
    }

    private class WebSocketRunner implements Runnable {

        private final BotConfiguration.ConnectionConfig connection;

        private WebSocketRunner(BotConfiguration.ConnectionConfig connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            try {
                OneBotConnection.start(
                        connection.host,
                        connection.port,
                        connection.authorization,
                        eventConsumer
                );
            } catch (InterruptedException ignore) {
                // do nothing
            } catch (Exception e) {
                e.printStackTrace();
            }
            logger.info("WebSocket 连接已关闭");
        }

    }

}
