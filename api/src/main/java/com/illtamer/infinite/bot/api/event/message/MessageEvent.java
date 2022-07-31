package com.illtamer.infinite.bot.api.event.message;

import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.annotation.Coordinates;
import com.illtamer.infinite.bot.api.entity.MessageSender;
import com.illtamer.infinite.bot.api.event.Event;
import com.illtamer.infinite.bot.api.handler.OpenAPIHandling;
import com.illtamer.infinite.bot.api.message.Message;
import com.illtamer.infinite.bot.api.message.MessageBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 消息上报事件
 * */
@Setter
@Getter
@ToString(callSuper = true)
@Coordinates(
        postType = Coordinates.PostType.MESSAGE,
        secType = "*"
)
public class MessageEvent extends Event {

    /**
     * 表示消息的子类型
     * <p>
     * 如果是好友则是 friend, 如果是群临时会话则是 group, 如果是在群中自身发送则是 group_self,
     * 正常群聊消息是 normal, 匿名消息是 anonymous, 系统提示 ( 如「管理员已禁止群内匿名聊天」 ) 是 notice
     * */
    @SerializedName("sub_type")
    private String subType;

    /**
     * 消息 ID
     * */
    @SerializedName("message_id")
    private Integer messageId;

    /**
     * 消息类型
     * <p>
     * group, private
     * */
    @SerializedName("message_type")
    private String messageType;

    /**
     * 发送者 QQ 号
     * */
    @SerializedName("user_id")
    private Long userId;

    /**
     * 一个消息链
     * // TODO 入方向的 Message.class Instance
     * */
    private String message;

    /**
     * CQ 码格式的消息
     * */
    @SerializedName("raw_message")
    private String rawMessage;

    /**
     * 字体
     * */
    private Integer font;

    /**
     * 发送者信息
     * */
    private transient MessageSender sender;

    public void reply(String message) {
        reply(MessageBuilder.json().text(message).build());
    }

    public void reply(Message message) {
        throw new UnsupportedOperationException();
    }

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
