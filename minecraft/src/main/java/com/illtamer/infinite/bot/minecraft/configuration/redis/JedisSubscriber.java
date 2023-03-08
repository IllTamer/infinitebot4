package com.illtamer.infinite.bot.minecraft.configuration.redis;

import com.illtamer.infinite.bot.minecraft.api.StaticAPI;
import redis.clients.jedis.JedisPubSub;

public class JedisSubscriber extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        // TODO
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
