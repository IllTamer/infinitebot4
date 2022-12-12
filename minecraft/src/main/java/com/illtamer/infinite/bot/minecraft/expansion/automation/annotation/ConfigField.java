package com.illtamer.infinite.bot.minecraft.expansion.automation.annotation;

import java.lang.annotation.*;

/**
 * 自动加载配置类字段注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigField {

    /**
     * 字段坐标
     * */
    String ref() default "";

    /**
     * 缺省值
     * */
    String value() default "";

}
