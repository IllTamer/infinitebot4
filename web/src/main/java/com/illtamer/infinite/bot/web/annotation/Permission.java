package com.illtamer.infinite.bot.web.annotation;

/**
 * 权限注解
 * <p>
 * 用于消息事件。消息发送者必须带有所需权限才能触发事件
 * */
public @interface Permission {

    /**
     * 权限节点
     * <p>
     * 以 properties 节点的形式配置，其中 * 为通配符
     * */
    String value() default "";

}
