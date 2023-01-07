package com.illtamer.infinite.bot.api;

import com.illtamer.infinite.bot.api.config.CQHttpWebSocketConfiguration;
import com.illtamer.infinite.bot.api.handler.GetMessageHandler;
import com.illtamer.infinite.bot.api.handler.OpenAPIHandling;
import com.illtamer.infinite.bot.api.util.HttpRequestUtil;

import java.util.Map;

public class Tests {

    public static void main(String[] args) {
        CQHttpWebSocketConfiguration.setHttpUri("http://47.117.136.149:5700");
        CQHttpWebSocketConfiguration.setAuthorization("root765743073");
//
        OpenAPIHandling.sendMessage("Hello World", 765743073);

//        Response<Map<String, Object>> response = new GroupMsgSendHandler()
//                .setGroupId(663950147L)
//                .setMessage(MessageBuilder.json().text("666").build())
//                .request();
//        System.out.println(response);
//        OpenAPIHandling.deleteMessage(352797457);
    }

}
