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
        if (!OneBotConnection.isRunning()) {
            log.warning("检测到 WebSocket 连接断开，尝试重连 perpetua 中");
            loginInfo = null;
        } else  {
            try {
                loginInfo = OpenAPIHandling.getLoginInfo();
            } catch (Exception ignore) {
                log.warning("获取账号信息失败，尝试重连 perpetua 中");
                loginInfo = null;
            }
        }
        if (loginInfo == null) {
            StaticAPI.reconnected();
        }
        lastRefreshTime = System.currentTimeMillis();
    }

}
