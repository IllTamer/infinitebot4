package com.illtamer.infinite.bot.minecraft;

import com.illtamer.infinite.bot.api.message.Message;
import com.illtamer.infinite.bot.api.message.MessageBuilder;

public class BootstrapTests {

    public static void main(String[] args) {
        final Message origin = MessageBuilder.json()
                .text("你好")
                .build();
        System.out.println(MessageBuilder.json()
                .customMessageNode("IllTamer", 765743073L, origin, null)
                .build());
    }

}
