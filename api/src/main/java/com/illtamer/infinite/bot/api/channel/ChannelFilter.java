package com.illtamer.infinite.bot.api.channel;

/**
 * 通道过滤器
 * */
@FunctionalInterface
public interface ChannelFilter<Param> {

    /**
     * 过滤规则
     * */
    boolean rule(ChannelContext context, Param param);

}
