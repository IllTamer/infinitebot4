package com.illtamer.infinite.bot.starter;

import com.illtamer.infinite.bot.api.config.CQHttpWebSocketConfiguration;
import com.illtamer.infinite.bot.api.event.Event;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;

import java.util.function.Consumer;

public class CQHttpWebSocketAutoConfiguration implements ApplicationRunner {

    private final Consumer<Event> eventConsumer;

    public CQHttpWebSocketAutoConfiguration(ApplicationContext context) {
        this.eventConsumer = context::publishEvent;
    }

    @Async
    @Override
    public void run(ApplicationArguments args) {
        CQHttpWebSocketConfiguration.start("http://zip1mask.top:5700", "ws://zip1mask.top:5701", "root765743073", eventConsumer);
    }

}
