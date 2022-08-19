package com.illtamer.infinite.bot.api.event.message;

import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.annotation.Coordinates;
import com.illtamer.infinite.bot.api.annotation.Untested;
import com.illtamer.infinite.bot.api.entity.Anonymous;
import com.illtamer.infinite.bot.api.entity.GroupMessageSender;
import com.illtamer.infinite.bot.api.event.QuickAction;
import com.illtamer.infinite.bot.api.handler.OpenAPIHandling;
import com.illtamer.infinite.bot.api.handler.QuickActionHandler;
import com.illtamer.infinite.bot.api.message.Message;
import com.illtamer.infinite.bot.api.message.MessageBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 群消息事件
 * */
@Setter
@Getter
@ToString(callSuper = true)
@Coordinates(
        postType = Coordinates.PostType.MESSAGE,
        secType = "group",
        subType = {"normal", "anonymous", "notice"}
)
public class GroupMessageEvent extends MessageEvent implements QuickAction {

    /**
     * 群号
     * */
    @SerializedName("group_id")
    private Long groupId;

    /**
     * 发送者信息
     * */
    private GroupMessageSender sender;

    /**
     * 匿名信息, 如果不是匿名消息则为 null
     * */
    private Anonymous anonymous;

    /**
     * 回复该条消息
     * */
    @Override
    public void reply(Message message) {
        reply(message, true);
    }

    /**
     * 回复该条消息
     * @deprecated #reply(String, boolean)
     * */
    @Deprecated
    public void reply(Message message, boolean atSender) {
        final MessageBuilder builder = MessageBuilder.json().reply(getMessageId());
        if (atSender)
            builder.at(getUserId());
        builder.addAll(message);
        OpenAPIHandling.sendGroupMessage(builder.build(), groupId);
    }

    /**
     * 撤回该条消息
     * @deprecated 快速操作API不稳定
     * */
    @Untested
    @Deprecated
    public void recall() {
        new QuickActionHandler(this)
                .addOperation("delete", true)
                .request();
    }

    /**
     * 把发送者踢出群组 (需要登录号权限足够)
     * <p>
     * 不拒绝此人后续加群请求, 发送者是匿名用户时无效
     * @deprecated 快速操作API不稳定
     * */
    @Untested
    @Deprecated
    public void kick() {
        new QuickActionHandler(this)
                .addOperation("kick", true)
                .request();
    }

    /**
     * 把发送者禁言30分钟
     * <p>
     * 对匿名用户也有效
     * @deprecated 快速操作API不稳定
     * */
    @Untested
    @Deprecated
    public void ban() {
        ban(30);
    }

    /**
     * 把发送者禁言指定时长
     * <p>
     * 对匿名用户也有效
     * @deprecated 快速操作API不稳定
     * */
    @Untested
    @Deprecated
    public void ban(int minute) {
        new QuickActionHandler(this)
                .addOperation("ban", true)
                .addOperation("ban_duration", minute)
                .request();
    }

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

    public GroupMessageSender getSender() {
        sender.setGroupId(groupId);
        return sender;
    }

}
