package com.illtamer.infinite.bot.api.channel;

/**
 * 通道异常处理接口
 * */
@FunctionalInterface
public interface ExceptionHandler {

    void handle(ChannelContext context, Exception e);

}
