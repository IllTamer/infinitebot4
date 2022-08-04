package com.illtamer.infinite.bot.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.illtamer.infinite.bot.api.event.message.MessageEvent;
import com.illtamer.infinite.bot.api.message.Message;
import com.illtamer.infinite.bot.api.message.MessageTypeAdapter;

public class Tests {

    public static void main(String[] args) {
//        CQHttpWebSocketConfiguration.setHttpUri("http://47.117.136.149:5700");
//        CQHttpWebSocketConfiguration.setAuthorization("root765743073");

        // MessageBuilder.json().text("Hello World").build()
//        System.out.println(OpenAPIHandling.getGroups());

        String json = "{\"message\":\"[CQ:image,file=75990ca9a3853bd3532e44b689d24675.image,subType=0,url=https://gchat.qpic.cn/gchatpic_new/765743073/663950147-2875492364-75990CA9A3853BD3532E44B689D24675/0?term=3]\"}";
        final Gson gson = new GsonBuilder().registerTypeAdapter(Message.class, new MessageTypeAdapter()).create();
        final MessageEvent event = gson.fromJson(json, MessageEvent.class);
        System.out.println(event.getMessage());
    }

}
