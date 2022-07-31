package com.illtamer.infinite.bot.api.event;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * 所有上报事件
 * */
@Setter
@Getter
@ToString
public class Event {

    /**
     * 表示该上报的类型, 消息, 请求, 通知, 或元事件
     * <p>
     * message, request, notice, meta_event
     * */
    @SerializedName("post_type")
    private String postType;

    /**
     * 收到事件的机器人的 QQ 号
     * */
    @SerializedName("self_id")
    private Long selfId;

    /**
     * 事件发生的时间戳
     * */
    private Date time;

}
