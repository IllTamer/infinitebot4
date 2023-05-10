package com.illtamer.infinite.bot.minecraft.util;

import com.illtamer.infinite.bot.api.event.Event;
import com.illtamer.infinite.bot.api.event.EventResolver;
import com.illtamer.infinite.bot.minecraft.configuration.redis.JedisSubscriber;
import lombok.experimental.UtilityClass;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.function.Consumer;

@UtilityClass
public class JedisUtil {

    public static final String CHANNEL = "infinite-bot-3-redis-channel";

    private static JedisPool pool;

    public static void init(String host, int port) {
        pool = new JedisPool(host, port);
    }

    public static void publish(Event event) {
        option(jedis -> jedis.publish(CHANNEL, EventResolver.GSON.toJson(event)));
    }

    public static void subscribe(Consumer<Event> consumer) {
        option(jedis -> jedis.subscribe(new JedisSubscriber(consumer), CHANNEL));
    }

    public static void option(Consumer<Jedis> consumer) {
        try (Jedis resource = pool.getResource()) {
            consumer.accept(resource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Jedis getJedis() {
        return pool.getResource();
    }

    public static void close() {
        pool.close();
    }

}
