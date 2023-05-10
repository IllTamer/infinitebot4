package com.illtamer.infinite.bot.minecraft.listener;

import com.illtamer.infinite.bot.minecraft.api.adapter.Bootstrap;
import com.illtamer.infinite.bot.minecraft.listener.handler.CommandHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BukkitCommandListener implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return CommandHelper.onCommand(new BukkitCommandSender(sender), args, Bootstrap.Type.BUKKIT);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return CommandHelper.onTabComplete(new BukkitCommandSender(sender), args);
    }

    private static class BukkitCommandSender implements com.illtamer.infinite.bot.minecraft.api.adapter.CommandSender {

        private final CommandSender sender;

        private BukkitCommandSender(CommandSender sender) {
            this.sender = sender;
        }

        @Override
        public boolean isOp() {
            return sender.isOp();
        }

        @Override
        public void sendMessage(String message) {
            sender.sendMessage(message);
        }

        @Override
        public void sendMessage(String[] messages) {
            sender.sendMessage(messages);
        }

    }

}
