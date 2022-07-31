package com.illtamer.infinite.bot.api.event;

import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.annotation.Coordinates;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 元事件上报事件
 * */
@Setter
@Getter
@ToString(callSuper = true)
@Coordinates(
        postType = Coordinates.PostType.META_EVENT,
        secType = "*"
)
public class MetaEvent extends Event {

    private static final String HEARTBEAT_MODIFY = "heartbeat";

    /**
     * 元数据类型
     * */
    @SerializedName("meta_event_type")
    private String metaEventType;

    public boolean isHeartbeat() {
        return HEARTBEAT_MODIFY.equals(metaEventType);
    }

}
