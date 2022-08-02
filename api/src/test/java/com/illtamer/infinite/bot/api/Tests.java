package com.illtamer.infinite.bot.api;

import com.illtamer.infinite.bot.api.config.CQHttpWebSocketConfiguration;
import com.illtamer.infinite.bot.api.entity.BotStatus;
import com.illtamer.infinite.bot.api.handler.StatusGetHandler;

public class Tests {

    public static void main(String[] args) {
        CQHttpWebSocketConfiguration.setHttpUri("http://47.117.136.149:5700");
        CQHttpWebSocketConfiguration.setAuthorization("root765743073");

        // MessageBuilder.json().text("Hello World").build()
        final Response response =
                new StatusGetHandler()
                        .request();

        final BotStatus status = StatusGetHandler.parse(response);

        System.out.println(status);
    }

}
