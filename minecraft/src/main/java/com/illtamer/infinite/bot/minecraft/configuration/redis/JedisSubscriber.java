package com.illtamer.infinite.bot.minecraft.configuration.redis;

import com.google.gson.JsonObject;
import com.illtamer.infinite.bot.api.event.Event;
import com.illtamer.infinite.bot.api.event.EventResolver;
import com.illtamer.infinite.bot.api.util.Maps;
import com.illtamer.infinite.bot.minecraft.api.StaticAPI;
import com.illtamer.infinite.bot.minecraft.api.event.LocalEvent;
import redis.clients.jedis.JedisPubSub;

import java.util.Map;
import java.util.function.Consumer;

public class JedisSubscriber extends JedisPubSub {

    private static final Map<String, Consumer<LocalEvent>> SYSTEM_IDENTIFIES = Maps.of(
            ConfigurationCenter.IDENTIFY, ConfigurationCenter::update
    );

    private final Consumer<Event> consumer;

    public JedisSubscriber(Consumer<Event> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onMessage(String channel, String message) {
        Event event = EventResolver.dispatchEvent(EventResolver.GSON.fromJson(message, JsonObject.class));
        // local redis event handler
        Consumer<LocalEvent> localAccept;
        if (event instanceof LocalEvent && (localAccept = SYSTEM_IDENTIFIES.get(((LocalEvent) event).getIdentify())) != null) {
            localAccept.accept((LocalEvent) event);
        } else {
            consumer.accept(event);
        }
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        StaticAPI.getInstance().getLogger().info("Redis channel [" + channel + "] append subscriber with index " + subscribedChannels);
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        StaticAPI.getInstance().getLogger().info("Redis channel [" + channel + "] unsubscribe with index " + subscribedChannels);
    }

}
