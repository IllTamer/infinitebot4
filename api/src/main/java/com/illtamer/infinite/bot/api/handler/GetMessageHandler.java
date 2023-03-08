package com.illtamer.infinite.bot.api.handler;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.Response;
import com.illtamer.infinite.bot.api.entity.Message;
import com.illtamer.infinite.bot.api.event.EventResolver;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * 获取消息 APIHandler
 * */
@Getter
public class GetMessageHandler extends AbstractAPIHandler<Map<String, Object>> {

    @SerializedName("message_id")
    private Integer messageId;

    public GetMessageHandler() {
        super("/get_msg");
    }

    public GetMessageHandler setMessageId(Integer messageId) {
        this.messageId = messageId;
        return this;
    }

    public static Message parse(@NotNull Response<Map<String, Object>> response) {
        return EventResolver.GSON.fromJson(new Gson().toJson(response.getData()), Message.class);
    }

}
