package com.illtamer.infinite.bot.minecraft.configuration;

import com.illtamer.infinite.bot.minecraft.api.BotScheduler;
import com.illtamer.infinite.bot.minecraft.api.StaticAPI;
import com.illtamer.infinite.bot.minecraft.configuration.config.BotConfiguration;
import com.illtamer.perpetua.sdk.event.Event;
import com.illtamer.perpetua.sdk.handler.OpenAPIHandling;
import com.illtamer.perpetua.sdk.websocket.OneBotConnection;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class BotNettyHolder {

    private volatile ExecutorService websocketExecutor;
    private final AtomicBoolean connecting = new AtomicBoolean(false);
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
        if (!connecting.compareAndSet(false, true)) {
            logger.info("已有重连任务进行中，跳过本次连接请求");
            return;
        }

        try {
            // 关闭旧的线程池，等待旧连接线程退出
            final ExecutorService oldExecutor = websocketExecutor;
            if (oldExecutor != null && !oldExecutor.isShutdown()) {
                oldExecutor.shutdownNow();
                try {
                    if (!oldExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                        logger.warning("旧 WebSocket 连接线程未能在 10 秒内退出");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // 创建新的线程池并提交连接任务
            final BotConfiguration.ConnectionConfig connection = BotConfiguration.connection;
            websocketExecutor = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "bot-websocket");
                t.setDaemon(true);
                return t;
            });
            websocketExecutor.execute(new WebSocketRunner(connection));

            BotScheduler.runTaskLater(() -> {
                try {
                    if (OneBotConnection.isRunning()) {
                        OpenAPIHandling.setClientName(connection.name);
                        StaticAPI.getClient().setClientName(connection.name);
                        logger.info("客户端昵称已设置为: " + connection.name);
                    }
                } catch (Exception e) {
                    logger.warning("设置客户端昵称失败: " + e.getMessage());
                }
            }, 5L);
        } finally {
            connecting.set(false);
        }
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
            final ExecutorService executor = websocketExecutor;
            if (executor != null) {
                executor.shutdownNow();
                logger.info("WebSocket 连接关闭 " + (executor.isShutdown() ? "成功" : "失败"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                // 被中断，正常退出
            } catch (Exception e) {
                logger.warning("WebSocket 连接异常: " + e.getMessage());
            }
            logger.info("WebSocket 连接已关闭");
        }

    }

}
