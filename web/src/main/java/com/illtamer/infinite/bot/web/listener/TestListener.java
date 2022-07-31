package com.illtamer.infinite.bot.web.listener;

import com.illtamer.infinite.bot.api.event.message.GroupMessageEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TestListener {

    @EventListener
    public void onTest(GroupMessageEvent event) {
//        Message message = MessageBuilder.json()
//                .text("Hello World")
//                .face(6)
//                .at(event.getUserId(), event.getSender().getNickname())
//                .build();
//        event.getSender().sendMessage(message);
//        event.reply(message);
    }

}
