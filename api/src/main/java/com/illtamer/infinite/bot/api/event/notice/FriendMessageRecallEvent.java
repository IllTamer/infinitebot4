package com.illtamer.infinite.bot.api.event.notice;

import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.annotation.Coordinates;
import com.illtamer.infinite.bot.api.handler.OpenAPIHandling;
import com.illtamer.infinite.bot.api.message.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 好友消息撤回事件
 * */
@Setter
@Getter
@ToString(callSuper = true)
@Coordinates(
        postType = Coordinates.PostType.NOTICE,
        secType = "friend_recall"
)
public class FriendMessageRecallEvent extends NoticeEvent {

    /**
     * 好友 QQ 号
     * */
    @SerializedName("user_id")
    private Long userId;

    /**
     * 被撤回的消息 ID
     * */
    @SerializedName("message_id")
    private Long messageId;

    /**
     * 向该消息发送者发送消息
     * @return 消息 ID
     * */
    public Double sendMessage(String message) {
        return OpenAPIHandling.sendMessage(message, userId);
    }

    /**
     * 向该消息发送者发送消息
     * @return 消息 ID
     * */
    public Double sendMessage(Message message) {
        return OpenAPIHandling.sendMessage(message, userId);
    }

}
