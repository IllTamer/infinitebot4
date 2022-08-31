package com.illtamer.infinite.bot.api.channel;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * 通道上下文实体类
 * <p>
 * 每个通道都具有该上下文对象，若不指定，则创建的子通道默认不继承父通道的上下文实例
 * */
public class ChannelContext {

    private final EventChannel<?> eventChannel;
    private final HashMap<String, Object> objectMap;

    public ChannelContext(EventChannel<?> eventChannel) {
        this.eventChannel = eventChannel;
        this.objectMap = new HashMap<>();
    }

    @Nullable
    public Object set(String key, Object value) {
        return objectMap.put(key, value);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T get(String key, T type) {
        return (T) get(key);
    }

    @Nullable
    public Object get(String key) {
        return objectMap.get(key);
    }

    public EventChannel<?> getEventChannel() {
        return eventChannel;
    }

}
