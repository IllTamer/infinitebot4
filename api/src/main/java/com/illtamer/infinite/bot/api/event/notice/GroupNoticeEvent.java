package com.illtamer.infinite.bot.api.event.notice;

import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.handler.OpenAPIHandling;
import com.illtamer.infinite.bot.api.message.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 群通知上报事件
 * */
@Setter
@Getter
@ToString(callSuper = true)
public abstract class GroupNoticeEvent extends NoticeEvent {

    /**
     * 群号
     * */
    @SerializedName("group_id")
    private Long groupId;

    /**
     * 触发者 QQ 号
     * */
    @SerializedName("user_id")
    private Long userId;

    /**
     * 向该消息发送者发送消息
     * @return 消息 ID
     * */
    public Double sendGroupMessage(String message) {
        return OpenAPIHandling.sendGroupMessage(message, groupId);
    }

    /**
     * 向该消息发送者发送消息
     * @return 消息 ID
     * */
    public Double sendGroupMessage(Message message) {
        return OpenAPIHandling.sendGroupMessage(message, groupId);
    }

}
