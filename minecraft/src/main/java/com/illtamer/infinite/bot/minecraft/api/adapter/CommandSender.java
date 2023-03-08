package com.illtamer.infinite.bot.minecraft.api.adapter;

public interface CommandSender {

    boolean isOp();

    void sendMessage(String message);

    void sendMessage(String[] messages);

}
