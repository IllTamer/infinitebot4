package com.illtamer.infinite.bot.minecraft.configuration;

import com.illtamer.infinite.bot.api.config.CQHttpWebSocketConfiguration;
import com.illtamer.infinite.bot.minecraft.api.EventExecutor;
import com.illtamer.infinite.bot.minecraft.configuration.config.BotConfiguration;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class BotNettyStarter extends JavaPlugin {

    @Getter
    protected static BotNettyStarter instance;

    protected Thread communicationThread;

    @Override
    public void onLoad() {
        instance = this;
        BotConfiguration.load(this);
        final BotConfiguration.ConnectionConfig connection = BotConfiguration.connection;
        communicationThread = new Thread(
                () -> {
                    try {
                        CQHttpWebSocketConfiguration.start(
                                String.format("http://%s:%d", connection.host, connection.httpPort),
                                String.format("ws://%s:%d", connection.host, connection.websocketPort),
                                connection.authorization,
                                EventExecutor::dispatchListener
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (CQHttpWebSocketConfiguration.isLoadSuccess()) {
                        getLogger().info("账号连接成功");
                    } else {
                        getLogger().warning("账号连接失败，停止加载，请检查控制台输出");
                    }
                },
                "InfiniteBot3-WebSocket-Communication-Thread"
        );
        communicationThread.start();
    }

    protected void close() {
        communicationThread.stop();
        getLogger().info("WebSocket 连接已关闭");
    }

}
