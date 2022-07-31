package com.illtamer.infinite.bot.api.event.message;

import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.annotation.Coordinates;
import com.illtamer.infinite.bot.api.entity.MessageSender;
import com.illtamer.infinite.bot.api.event.QuickAction;
import com.illtamer.infinite.bot.api.handler.QuickActionHandler;
import com.illtamer.infinite.bot.api.message.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 私聊消息事件
 * */
@Setter
@Getter
@ToString(callSuper = true)
@Coordinates(
        postType = Coordinates.PostType.MESSAGE,
        secType = "private",
        subType = {"friend", "group", "group_self", "other"}
)
public class PrivateMessageEvent extends MessageEvent implements QuickAction {

    /**
     * 临时会话来源
     * */
    @SerializedName("temp_source")
    private Integer tempSource;

    /**
     * 发送者信息
     * */
    private MessageSender sender;

    /**
     * 快捷回复该条消息
     * */
    @Override
    public void reply(Message message) {
        new QuickActionHandler(this)
                .addOperation("reply", message)
                .addOperation("auto_escape", message.isTextOnly())
                .request();
    }

}
