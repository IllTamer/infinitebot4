package com.illtamer.infinite.bot.api.handler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.message.Message;
import lombok.Getter;

import java.util.Map;

@Getter
public class PrivateMsgSendHandler extends AbstractAPIHandler<Map<String, Object>> {

    /**
     * 对方 QQ 号
     * */
    @SerializedName("user_id")
    private Long userId;

    @SerializedName("group_id")
    private Long groupId;

    private JsonArray message;

    @SerializedName("auto_escape")
    private boolean autoEscape;

    public PrivateMsgSendHandler() {
        super("/send_private_msg");
    }

    public PrivateMsgSendHandler setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public PrivateMsgSendHandler setGroupId(Long groupId) {
        this.groupId = groupId;
        return this;
    }

    public PrivateMsgSendHandler setMessage(Message message) {
        this.message = new Gson().fromJson(message.toString(), JsonArray.class);
        this.autoEscape = message.isTextOnly();
        return this;
    }

}
