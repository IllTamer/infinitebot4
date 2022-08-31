package com.illtamer.infinite.bot.api.channel;

import com.illtamer.infinite.bot.api.Pair;
import com.illtamer.infinite.bot.api.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

/**
 * 事件通道
 * */
public class EventChannel<Type extends Event> {

    // 当前通道等级 [0, +∞)
    private final int level;
    private final ChannelContext context;
    private final Class<Type> eventClass;

    // 当前通道下存在的所有子通道实例
    protected final List<EventChannel<?>> subEventChannels = new LinkedList<>();
    protected final List<ChannelFilter<Type>> filterList = new LinkedList<>();
    protected final List<Pair<EventHandler<Type>, ChannelPriority>> eventConsumerList = new LinkedList<>();
    protected final List<ExceptionHandler> exceptionHandlerList = new LinkedList<>();

    public EventChannel(Class<Type> eventClass) {
        this(0, eventClass, null);
    }

    protected EventChannel(int level, Class<Type> eventClass, @Nullable ChannelContext context) {
        this.level = level;
        this.eventClass = eventClass;
        this.context = context == null ? new ChannelContext(this) : context;
//        if (level == 0) EventExecutor.registerRootEventChannel(this);
    }

    /**
     * 注册事件过滤器
     * */
    public EventChannel<Type> filter(ChannelFilter<Type> channelFilter) {
        filterList.add(channelFilter);
        return this;
    }

    /**
     * 注册事件处理器
     * */
    public EventChannel<Type> eventHandler(EventHandler<Type> eventConsumer) {
        return eventHandler(eventConsumer, ChannelPriority.DEFAULT);
    }

    /**
     * 注册事件处理器
     * @param priority 统一等级下 EventChannel 中事件调度的优先级设置。父级 channel 总比子级先触发 TODO
     * */
    public EventChannel<Type> eventHandler(EventHandler<Type> eventConsumer, ChannelPriority priority) {
        eventConsumerList.add(new Pair<>(eventConsumer, priority));
        return this;
    }

    /**
     * 注册异常处理器
     * */
    public EventChannel<Type> exceptionHandler(ExceptionHandler exceptionHandler) {
        exceptionHandlerList.add(exceptionHandler);
        return this;
    }

    /**
     * 创建子事件通道
     * <p>
     * 创建当前事件通道的子事件通道，若当前通道被关闭，相应子事件通道也一同关闭 TODO
     * @param eventClass 监听事件的种类
     * */
    public <T extends Event> EventChannel<T> subscribeChannel(Class<T> eventClass) {
        return subscribeChannel(eventClass, false);
    }

    /**
     * 创建子事件通道
     * <p>
     * 创建当前事件通道的子事件通道，若当前通道被关闭，相应子事件通道也一同关闭 TODO
     * @param eventClass 监听事件的种类
     * @param extendContext 是否继承父类上下文对象
     * */
    public <T extends Event> EventChannel<T> subscribeChannel(Class<T> eventClass, boolean extendContext) {
        final EventChannel<T> subEventChannel = new EventChannel<>(level + 1, eventClass,  extendContext ? context : null);
        subEventChannels.add(subEventChannel);
        return subEventChannel;
    }

    /**
     * 关闭事件通道
     * @apiNote 若存在子事件通道，则将会一同关闭
     * */
    public void close() {
        // TODO
    }

}
