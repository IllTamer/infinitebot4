package com.illtamer.infinite.bot.minecraft.api;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.illtamer.infinite.bot.minecraft.util.ThreadPoolUtil;

import java.util.concurrent.*;

public class BotScheduler {

    public static final ExecutorService IO_INTENSIVE;
    public static final ScheduledExecutorService TIMER;

    public static void runTask(Runnable runnable) {
        IO_INTENSIVE.submit(runnable);
    }

    public static <V> void runTask(Callable<V> callable) {
        IO_INTENSIVE.submit(callable);
    }

    public static <T> Future<T> runTask(Runnable runnable, T t) {
        return IO_INTENSIVE.submit(runnable, t);
    }

    public static ScheduledFuture<?> runTaskLater(Runnable runnable, long delaySeconds) {
        return TIMER.schedule(runnable, delaySeconds, TimeUnit.SECONDS);
    }

    public static ScheduledFuture<?> runTaskTimer(Runnable runnable, long initDelay, long periodSeconds) {
        return TIMER.scheduleAtFixedRate(runnable, initDelay, periodSeconds, TimeUnit.SECONDS);
    }

    public static void close() {
        IO_INTENSIVE.shutdownNow();
        TIMER.shutdownNow();
    }

    static {
        TIMER = new ScheduledThreadPoolExecutor(1,
                new ThreadFactoryBuilder()
                        .setNameFormat("bot-schedule-%d")
                        .setUncaughtExceptionHandler((t, e) -> e.printStackTrace())
                        .build());
        IO_INTENSIVE = new ThreadPoolExecutor(
                3,
                ThreadPoolUtil.poolSize(0.90),
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new ThreadFactoryBuilder()
                        .setNameFormat("bot-thread-%d")
                        .setUncaughtExceptionHandler((t, e) -> e.printStackTrace())
                        .build());
    }

}
