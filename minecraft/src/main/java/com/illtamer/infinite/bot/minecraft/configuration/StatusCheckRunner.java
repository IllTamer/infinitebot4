package com.illtamer.infinite.bot.minecraft.configuration;

import com.illtamer.infinite.bot.api.config.CQHttpWebSocketConfiguration;
import com.illtamer.infinite.bot.api.entity.BotStatus;
import com.illtamer.infinite.bot.api.handler.OpenAPIHandling;
import com.illtamer.infinite.bot.minecraft.api.StaticAPI;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

/**
 * 机器人状态检查 Runnable
 * */
public class StatusCheckRunner implements Runnable {

    @Getter
    private static long lastRefreshTime;
    @Getter
    @Nullable
    private static BotStatus status;

    private final Logger log;

    public StatusCheckRunner(Logger logger) {
        this.log = logger;
    }

    @Override
    public void run() {
        if (!CQHttpWebSocketConfiguration.isRunning()) {
            log.warning("检测到 WebSocket 连接断开，尝试重连 go-cqhttp 中");
            status = null;
        } else  {
            try {
                status = OpenAPIHandling.getStatus();
            } catch (Exception ignore) {
                log.warning("获取账号信息失败，尝试重连 go-cqhttp 中");
                status = null;
            }
        }
        if (status == null) {
            StaticAPI.reconnected();
        }
        lastRefreshTime = System.currentTimeMillis();
    }

}
