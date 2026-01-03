package com.illtamer.infinite.bot.minecraft.configuration;

import com.illtamer.infinite.bot.minecraft.api.StaticAPI;
import com.illtamer.perpetua.sdk.entity.transfer.entity.LoginInfo;
import com.illtamer.perpetua.sdk.entity.transfer.entity.Status;
import com.illtamer.perpetua.sdk.handler.OpenAPIHandling;
import com.illtamer.perpetua.sdk.websocket.OneBotConnection;
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
    private static LoginInfo loginInfo;

    private final Logger log;

    public StatusCheckRunner(Logger logger) {
        this.log = logger;
    }

    @Override
    public void run() {
        lastRefreshTime = System.currentTimeMillis();

        if (!OneBotConnection.isRunning()) {
            doReconnect("检测到 WebSocket 连接断开，尝试重连 perpetua 中");
            return;
        }

        Status status = null;
        try {
            status = OpenAPIHandling.getStatus();
        } catch (Exception ignored) {}
        if (status == null || !status.getOnline() || !status.getGood()) {
            doReconnect("账号状态异常，尝试重连 perpetua 中");
            return;
        }

        try {
            loginInfo = OpenAPIHandling.getLoginInfo();
        } catch (Exception ignore) {
            doReconnect("获取账号信息失败，尝试重连 perpetua 中");
            return;
        }
    }

    private void doReconnect(String reason) {
        log.warning(reason);
        loginInfo = null;
        StaticAPI.reconnected();
    }

}
