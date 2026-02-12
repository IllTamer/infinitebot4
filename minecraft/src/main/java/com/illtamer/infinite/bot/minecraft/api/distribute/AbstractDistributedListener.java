package com.illtamer.infinite.bot.minecraft.api.distribute;

import com.illtamer.infinite.bot.minecraft.api.EventExecutor;
import com.illtamer.infinite.bot.minecraft.api.IExpansion;
import com.illtamer.infinite.bot.minecraft.api.distribute.DistributedEventContext;
import com.illtamer.infinite.bot.minecraft.api.distribute.DistributedEventProcessor;
import com.illtamer.infinite.bot.minecraft.api.event.Listener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 分布式事件监听抽象类
 * */
@Getter
@Slf4j
public abstract class AbstractDistributedListener<T extends Serializable> implements Listener {

    protected final IExpansion expansion;
    protected final DistributedEventProcessor<T> processor;

    public AbstractDistributedListener(IExpansion expansion, Class<T> dataType) {
        this.expansion = expansion;
        this.processor = new DistributedEventProcessor<>(expansion, dataType);
        // 子类实现具体的本地逻辑注册
        getProcessor().registerHandler(getIdentifier(), this::handle);
        // 注册分布式通讯监听
        EventExecutor.registerEvents(processor.createListener(), expansion);
    }

    /**
     * 注册本地执行逻辑
     */
    abstract public T handle(DistributedEventContext context);

    /**
     * 获取触发该事件的关键词
     */
    abstract public String getIdentifier();

    /**
     * 注册当前监听类
     * */
    public void register() {
        EventExecutor.registerEvents(this, expansion);
    }

}
