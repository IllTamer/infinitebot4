package com.illtamer.infinite.bot.minecraft.api.event;

import com.illtamer.infinite.bot.api.event.Event;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 本地 Redis 事件
 * */
@Setter
@Getter
@ToString(callSuper = true)
@Builder
public class LocalEvent extends Event {

    /**
     * 标识符
     * */
    private String identify;

    /**
     * 传递的数据
     * */
    private String data;

    public <T> T getData(Class<T> clazz) {
        return clazz.cast(data);
    }

}
