package com.illtamer.infinite.bot.api.channel;

import com.illtamer.infinite.bot.api.event.Event;

/**
 * 通道事件处理接口
 * */
@FunctionalInterface
public interface EventHandler<Type extends Event> {

    void handle(ChannelContext context, Type event) throws Exception;

    /**
     * 关闭上下文所关联的 EventHandler
     * */
    default void close(ChannelContext context) {
        context.getEventChannel().close();
    }

}
