package com.illtamer.infinite.bot.minecraft.listener;

import com.illtamer.infinite.bot.minecraft.listener.handler.CommandHelper;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;

public class BungeeCommandListener extends Command {

    public BungeeCommandListener() {
        super("InfiniteBot3", null, "ib3");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        CommandHelper.onCommand(new com.illtamer.infinite.bot.minecraft.api.adapter.CommandSender() {
            @Override
            public boolean isOp() {
                return sender.hasPermission("ib3-op-test-permission");
            }

            @Override
            public void sendMessage(String message) {
                TextComponent comp = new TextComponent();
                comp.setText(message);
                sender.sendMessage(comp);
            }

            @Override
            public void sendMessage(String[] messages) {
                TextComponent[] components = Arrays.stream(messages).map(message -> {
                    TextComponent comp = new TextComponent();
                    comp.setText(message);
                    return comp;
                }).toArray(TextComponent[]::new);
                sender.sendMessage(components);
            }
        }, args);
    }

}
