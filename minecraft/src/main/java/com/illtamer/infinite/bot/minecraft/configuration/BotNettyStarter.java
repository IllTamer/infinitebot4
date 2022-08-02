package com.illtamer.infinite.bot.minecraft.configuration;

import com.illtamer.infinite.bot.api.config.CQHttpWebSocketConfiguration;
import com.illtamer.infinite.bot.minecraft.api.EventExecutor;
import com.illtamer.infinite.bot.minecraft.configuration.config.BotConfiguration;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BotNettyStarter extends JavaPlugin {

    @Getter
    protected static BotNettyStarter instance;

    protected ExecutorService websocketExecutor = Executors.newSingleThreadExecutor();

    @Override
    public void onLoad() {
        instance = this;
        BotConfiguration.load(instance);
        connect();
    }

    /**
     * 开启 WebSocket 连接
     * */
    public void connect() {
        final BotConfiguration.ConnectionConfig connection = BotConfiguration.connection;
        websocketExecutor.execute(new WebSocketRunner(connection));
    }

    protected void checkConnection() {
        if (CQHttpWebSocketConfiguration.isRunning()) {
            getLogger().info("账号连接成功");
        } else {
            getLogger().warning("账号连接失败，请检查控制台输出处理，或等待自动重连");
        }
    }

    protected void close() {
        websocketExecutor.shutdown();
        getLogger().info("WebSocket 连接关闭 " + (websocketExecutor.isShutdown() ? "成功" : "失败"));
    }

    private class WebSocketRunner implements Runnable {

        private final BotConfiguration.ConnectionConfig connection;

        private WebSocketRunner(BotConfiguration.ConnectionConfig connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            try {
                CQHttpWebSocketConfiguration.start(
                        String.format("http://%s:%d", connection.host, connection.httpPort),
                        String.format("ws://%s:%d", connection.host, connection.websocketPort),
                        connection.authorization,
                        EventExecutor::dispatchListener
                );
                getLogger().info("WebSocket 连接已关闭");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
