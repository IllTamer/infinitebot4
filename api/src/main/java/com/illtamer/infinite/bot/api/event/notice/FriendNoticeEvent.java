package com.illtamer.infinite.bot.api.event.notice;

import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.annotation.Coordinates;
import com.illtamer.infinite.bot.api.handler.OpenAPIHandling;
import com.illtamer.infinite.bot.api.message.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 好友添加提醒事件
 * */
@Setter
@Getter
@ToString(callSuper = true)
@Coordinates(
        postType = Coordinates.PostType.NOTICE,
        secType = "friend_add"
)
public class FriendNoticeEvent extends NoticeEvent {

    /**
     * 新添加好友 QQ 号
     * */
    @SerializedName("user_id")
    private Long userId;

    /**
     * 向该消息发送者发送消息
     * @return 消息 ID
     * */
    public Integer sendMessage(String message) {
        return OpenAPIHandling.sendMessage(message, userId);
    }

    /**
     * 向该消息发送者发送消息
     * @return 消息 ID
     * */
    public Integer sendMessage(Message message) {
        return OpenAPIHandling.sendMessage(message, userId);
    }

}
