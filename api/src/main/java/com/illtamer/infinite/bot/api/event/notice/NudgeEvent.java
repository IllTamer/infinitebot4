package com.illtamer.infinite.bot.api.event.notice;

import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.annotation.Coordinates;
import com.illtamer.infinite.bot.api.handler.OpenAPIHandling;
import com.illtamer.infinite.bot.api.message.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

/**
 * 戳一戳事件
 * */
@Setter
@Getter
@ToString(callSuper = true)
@Coordinates(
        postType = Coordinates.PostType.NOTICE,
        secType = "notify",
        subType = "poke"
)
public class NudgeEvent extends NoticeEvent {

    /**
     * 提示类型
     * <p>
     * poke
     * */
    @SerializedName("sub_type")
    private String subType;

    /**
     * 发送者 QQ 号
     * @apiNote 当事件为好友戳一戳时赋值此字段
     * */
    @SerializedName("sender_id")
    @Nullable
    private Long senderId;

    /**
     * 群号
     * @apiNote 当事件为群组戳一戳时赋值此字段
     * */
    @SerializedName("group_id")
    @Nullable
    private Long groupId;

    /**
     * 发送者 QQ 号
     * */
    @SerializedName("user_id")
    private Long userId;

    /**
     * 被戳者 QQ 号
     * */
    @SerializedName("target_id")
    private Long targetId;

    boolean isGroupEvent() {
        return groupId != null;
    }

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
