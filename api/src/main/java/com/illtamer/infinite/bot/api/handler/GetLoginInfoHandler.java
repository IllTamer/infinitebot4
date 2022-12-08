package com.illtamer.infinite.bot.api.handler;

import com.google.gson.Gson;
import com.illtamer.infinite.bot.api.entity.LoginInfo;

import java.util.Map;

public class GetLoginInfoHandler extends AbstractAPIHandler<Map<String, Object>> {

    public GetLoginInfoHandler() {
        super("/get_login_info");
    }

    public static LoginInfo parse(Map<String, Object> response) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(response), LoginInfo.class);
    }

}
