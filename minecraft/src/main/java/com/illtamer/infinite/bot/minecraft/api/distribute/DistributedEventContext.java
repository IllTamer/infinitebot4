package com.illtamer.infinite.bot.minecraft.api.distribute;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分布式事件上下文
 * 包含事件执行所需的所有上下文信息
 */
@Data
public class DistributedEventContext {

    /**
     * 事件参数
     */
    private Map<String, Object> params = new ConcurrentHashMap<>();

    /**
     * 事件源客户端ID
     */
    private String sourceClientId;

    /**
     * 当前客户端ID
     */
    private String currentClientId;

    /**
     * 事件时间戳
     */
    private long timestamp;

    /**
     * 附加数据
     */
    private Object attachment;

    public DistributedEventContext() {
        this.timestamp = System.currentTimeMillis();
    }

    public DistributedEventContext(String sourceClientId, String currentClientId) {
        this();
        this.sourceClientId = sourceClientId;
        this.currentClientId = currentClientId;
    }

    /**
     * 获取参数
     */
    public <T> T getParam(String key) {
        return (T) params.get(key);
    }

    /**
     * 获取参数，带默认值
     */
    public <T> T getParam(String key, T defaultValue) {
        return (T) params.getOrDefault(key, defaultValue);
    }

    /**
     * 设置参数
     */
    public DistributedEventContext setParam(String key, Object value) {
        this.params.put(key, value);
        return this;
    }

    /**
     * 获取字符串参数
     */
    public String getStringParam(String key) {
        return getParam(key);
    }

    /**
     * 获取整数参数
     */
    public Integer getIntegerParam(String key) {
        return getParam(key);
    }

    /**
     * 获取布尔参数
     */
    public Boolean getBooleanParam(String key) {
        return getParam(key);
    }
}