package com.illtamer.infinite.bot.api.handler;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.Map;

/**
 * 撤回消息 APIHandler
 * */
@Getter
public class DeleteMsgHandler extends AbstractAPIHandler<Map<String, Object>> {

    @SerializedName("message_id")
    private Integer messageId;

    public DeleteMsgHandler() {
        super("/delete_msg");
    }

    public DeleteMsgHandler setMessageId(Integer messageId) {
        this.messageId = messageId;
        return this;
    }

}
