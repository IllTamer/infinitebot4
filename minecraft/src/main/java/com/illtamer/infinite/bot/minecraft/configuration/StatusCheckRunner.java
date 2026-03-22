package com.illtamer.infinite.bot.minecraft.configuration;

import com.illtamer.infinite.bot.minecraft.api.StaticAPI;
import com.illtamer.perpetua.sdk.entity.transfer.entity.LoginInfo;
import com.illtamer.perpetua.sdk.entity.transfer.entity.Status;
import com.illtamer.perpetua.sdk.handler.OpenAPIHandling;
import com.illtamer.perpetua.sdk.websocket.OneBotConnection;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * 机器人状态检查 Runnable
 * */
public class StatusCheckRunner implements Runnable {

    /**
     * API 调用超时时间（秒）
     * */
    private static final int API_TIMEOUT_SECONDS = 10;

    /**
     * 基础检查间隔（秒），与 BotScheduler 注册的周期一致
     * */
    private static final int BASE_INTERVAL_SECONDS = 30;

    /**
     * 最大退避间隔（秒）
     * */
    private static final int MAX_BACKOFF_SECONDS = 300;

    @Getter
    private static long lastRefreshTime;
    @Getter
    @Nullable
    private static LoginInfo loginInfo;

    private final Logger log;
    private final AtomicInteger consecutiveFailures = new AtomicInteger(0);
    private volatile long nextCheckTime = 0;

    public StatusCheckRunner(Logger logger) {
        this.log = logger;
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();

        // 退避检查：如果还没到下次检查时间，跳过
        if (now < nextCheckTime) {
            return;
        }

        lastRefreshTime = now;

        if (!OneBotConnection.isRunning()) {
            doReconnect("检测到 WebSocket 连接断开，尝试重连 perpetua 中");
            return;
        }

        // 带超时的状态检查，防止阻塞 TIMER 线程
        Status status;
        try {
            status = CompletableFuture.supplyAsync(OpenAPIHandling::getStatus)
                    .get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            doReconnect("获取状态超时或异常，尝试重连 perpetua 中");
            return;
        }

        if (status == null || !status.getOnline() || !status.getGood()) {
            doReconnect("账号状态异常，尝试重连 perpetua 中");
            return;
        }

        // 带超时的登录信息获取
        try {
            loginInfo = CompletableFuture.supplyAsync(OpenAPIHandling::getLoginInfo)
                    .get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            doReconnect("获取账号信息失败，尝试重连 perpetua 中");
            return;
        }

        // 检查通过，重置失败计数
        int prev = consecutiveFailures.getAndSet(0);
        if (prev > 0) {
            log.info("连接状态恢复正常");
        }
    }

    private void doReconnect(String reason) {
        int failures = consecutiveFailures.incrementAndGet();

        // 计算退避时间：30s, 60s, 120s, 240s, 300s(max)
        long backoffSeconds = Math.min(
                BASE_INTERVAL_SECONDS * (1L << Math.min(failures - 1, 4)),
                MAX_BACKOFF_SECONDS
        );
        nextCheckTime = System.currentTimeMillis() + (backoffSeconds * 1000);

        log.warning(String.format("%s (第 %d 次失败，下次重试间隔 %d 秒)", reason, failures, backoffSeconds));
        loginInfo = null;
        StaticAPI.reconnected();
    }

}
