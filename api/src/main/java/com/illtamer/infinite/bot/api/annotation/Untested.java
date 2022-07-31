package com.illtamer.infinite.bot.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 未经测试的方法注解
 * @apiNote 仅作标识
 * */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Untested {
}
