package com.illtamer.infinite.bot.minecraft.expansion.automation.annotation;

import java.lang.annotation.*;

/**
 * 自动加载配置类类注解
 * */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigClass {

    /**
     * 配置类对应配置文件的名称 (含后缀)
     * */
    String name() default "";

}
