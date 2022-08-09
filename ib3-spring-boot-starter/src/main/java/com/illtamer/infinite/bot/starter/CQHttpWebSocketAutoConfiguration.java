package com.illtamer.infinite.bot.starter;

import com.illtamer.infinite.bot.api.config.CQHttpWebSocketConfiguration;
import com.illtamer.infinite.bot.api.event.Event;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;

import java.util.function.Consumer;

public class CQHttpWebSocketAutoConfiguration implements ApplicationRunner {

    private final BotProperties botProperties;
    private final Consumer<Event> eventConsumer;

    public CQHttpWebSocketAutoConfiguration(
            BotProperties botProperties,
            ApplicationContext context
    ) {
        this.botProperties = botProperties;
        this.eventConsumer = context::publishEvent;
    }

    @Async
    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        CQHttpWebSocketConfiguration.start(
                botProperties.getHttpUri(),
                botProperties.getWsUri(),
                botProperties.getAuthorization(),
                eventConsumer
        );
    }

}
