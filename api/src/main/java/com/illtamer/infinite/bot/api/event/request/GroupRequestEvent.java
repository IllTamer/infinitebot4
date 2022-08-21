package com.illtamer.infinite.bot.api.event.request;

import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.annotation.Coordinates;
import com.illtamer.infinite.bot.api.annotation.Untested;
import com.illtamer.infinite.bot.api.event.QuickAction;
import com.illtamer.infinite.bot.api.handler.OpenAPIHandling;
import com.illtamer.infinite.bot.api.handler.QuickActionHandler;
import com.illtamer.infinite.bot.api.message.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

/**
 * 加群请求／邀请事件
 * */
@Setter
@Getter
@ToString(callSuper = true)
@Coordinates(
        postType = Coordinates.PostType.REQUEST,
        secType = "group",
        subType = {"add", "invite"}
)
public class GroupRequestEvent extends RequestEvent implements QuickAction {

    /**
     * 请求子类型, 分别表示加群请求、邀请登录号入群
     * <p>
     * add、invite
     * */
    @SerializedName("sub_type")
    private String subType;

    /**
     * 群号
     * */
    @SerializedName("group_id")
    private Long groupId;

    /**
     * 发送请求的 QQ 号
     * */
    @SerializedName("user_id")
    private Long userId;

    /**
     * 验证信息
     * */
    private String comment;

    /**
     * 请求 flag, 在调用处理请求的 API 时需要传入
     * */
    private String flag;

    /**
     * 同意请求
     * */
    @Untested
    public void approve() {
        new QuickActionHandler(this)
                .addOperation("approve", true)
                .request();
    }

    /**
     * 拒绝请求
     * @param reason 拒绝理由
     * */
    @Untested
    public void reject(@Nullable String reason) {
        QuickActionHandler handler = new QuickActionHandler(this)
                .addOperation("approve", false);
        if (reason != null)
            handler.addOperation("reason", reason);
        handler.request();
    }

    /**
     * 向该消息发送者发送消息
     * @return 消息 ID
     * */
    public Integer sendGroupMessage(String message) {
        return OpenAPIHandling.sendGroupMessage(message, groupId);
    }

    /**
     * 向该消息发送者发送消息
     * @return 消息 ID
     * */
    public Integer sendGroupMessage(Message message) {
        return OpenAPIHandling.sendGroupMessage(message, groupId);
    }

}
