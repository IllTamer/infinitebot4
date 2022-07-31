package com.illtamer.infinite.bot.api;

import com.google.gson.Gson;
import com.illtamer.infinite.bot.api.config.CQHttpWebSocketConfiguration;
import com.illtamer.infinite.bot.api.handler.GroupForwardSendHandler;
import com.illtamer.infinite.bot.api.handler.GroupMsgSendHandler;
import com.illtamer.infinite.bot.api.handler.PrivateMsgSendHandler;
import com.illtamer.infinite.bot.api.message.MessageBuilder;

public class Tests {

    public static void main(String[] args) {
        CQHttpWebSocketConfiguration.setHttpUri("http://zip1mask.top:5700");
        CQHttpWebSocketConfiguration.setAuthorization("root765743073");

        // MessageBuilder.json().text("Hello World").build()
        final Response response =
                new GroupMsgSendHandler()
//                new PrivateMsgSendHandler()
                .setGroupId(663950147L)
//                .setUserId(765743073L)
                .setMessage(MessageBuilder.json()
                        .text("https://www.gstatic.com/inputtools/images/ita_sprite8.png")
//                        .text("666")
                        .build()
                )
                .request();

        System.out.println(response);
    }

}
