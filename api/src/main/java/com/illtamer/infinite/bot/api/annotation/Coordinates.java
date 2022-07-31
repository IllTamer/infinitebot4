package com.illtamer.infinite.bot.api.annotation;

import com.illtamer.infinite.bot.api.exception.TypeParseException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.annotation.*;

/**
 * 事件坐标标记注解
 * <p>
 * 该注解按事件继承关系，需从上到下配置该事件对应的go-cqhttp事件类型
 * */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Coordinates {

    String POST_TYPE = "post_type";

    String SUB_TYPE = "sub_type";

    /**
     * 表示该上报的类型, 消息, 请求, 通知, 或元事件
     * <p>
     * message, request, notice, meta_event
     * */
    PostType postType();

    /**
     * 上报类型的二级类型
     * <p>
     * message_type.?
     * request_type.?
     * notice_type.?
     * meta_event_type.?
     * @return * - 全匹配
     * */
    String[] secType() default "";

    /**
     * 二级类型的子类型
     * @return * - 全匹配
     * */
    String[] subType() default "";

    /**
     * 上报的类型
     * */
    @Getter
    @AllArgsConstructor
    enum PostType {

        /**
         * 消息
         * */
        MESSAGE("message"),

        /**
         * 请求
         * */
        REQUEST("request"),

        /**
         * 通知
         * */
        NOTICE("notice"),

        /**
         * 元事件
         * */
        META_EVENT("meta_event");

        private final String value;

        public String parseSecType() {
            return this.value + "_type";
        }

        public static PostType format(String name) {
            if (MESSAGE.value.equals(name))
                return MESSAGE;
            else if (REQUEST.value.equals(name))
                return REQUEST;
            else if (NOTICE.value.equals(name))
                return NOTICE;
            else if (META_EVENT.value.equals(name))
                return META_EVENT;
            throw new TypeParseException("Unknown type: " + name);
        }

    }

}
