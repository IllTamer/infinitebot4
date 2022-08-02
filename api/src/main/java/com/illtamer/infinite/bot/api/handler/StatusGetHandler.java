package com.illtamer.infinite.bot.api.handler;

import com.google.gson.Gson;
import com.illtamer.infinite.bot.api.Response;
import com.illtamer.infinite.bot.api.entity.BotStatus;
import org.jetbrains.annotations.NotNull;

/**
 * 机器人状态查看
 * */
public class StatusGetHandler extends AbstractAPIHandler {

    public StatusGetHandler() {
        super("/get_status");
    }

    @NotNull
    public static BotStatus parse(Response response) {
        final Gson gson = new Gson();
        return gson.fromJson(gson.toJson(response.getData()), BotStatus.class);
    }

}
