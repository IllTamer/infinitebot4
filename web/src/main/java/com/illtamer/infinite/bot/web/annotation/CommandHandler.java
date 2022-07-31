package com.illtamer.infinite.bot.web.annotation;

import java.lang.annotation.*;

/**
 * 指令处理方法注解
 * <p>
 * 该方法标注的监听器方法事件必须为 {@link com.illtamer.infinite.bot.api.event.message.MessageEvent} 或其子类
 * */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandHandler {

    /**
     * 指令前缀
     * */
    String prefix() default "/";

    /**
     * 参数长度
     * */
    int length() default -1;

}
