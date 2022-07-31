package com.illtamer.infinite.bot.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@EnableAsync
@Configuration
public class ThreadPoolConfiguration {

    public static final ScheduledExecutorService DELAY_POOL = Executors.newScheduledThreadPool(1);

    @Bean("programThreadPool")
    public Executor getProgramThreadPool() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setMaxPoolSize(128);
        taskExecutor.setQueueCapacity(64);
        taskExecutor.initialize();
        return taskExecutor;
    }

}
