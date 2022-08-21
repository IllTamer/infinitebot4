package com.illtamer.infinite.bot.minecraft.listener;

import com.illtamer.infinite.bot.api.entity.BotStatus;
import com.illtamer.infinite.bot.minecraft.Bootstrap;
import com.illtamer.infinite.bot.minecraft.configuration.StatusCheckRunner;
import com.illtamer.infinite.bot.minecraft.configuration.config.BotConfiguration;
import com.illtamer.infinite.bot.minecraft.expansion.ExpansionLoader;
import com.illtamer.infinite.bot.minecraft.util.ValidUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandListener implements TabExecutor {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm:ss");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) return true;
        if (args.length == 1) {
            if ("help".equalsIgnoreCase(args[0])) {
                sender.sendMessage(new String[] {
                        "ib3:",
                        " ├──help: 获取帮助",
                        " ├──check: 检查账号的连接状态",
                        " ├──reload: 重载本体配置文件",
                        " ├──expansions",
                        " │   ├──list: 列出所有加载的附属名称",
                        " │   └──reload: 重载 expansions 目录下所有附属",
                        " ├──load",
                        " │   └──[附属文件名]: 加载名称对应附属",
                        " └──unload",
                        "     └──[附属名称]: 卸载名称对应附属"
                });
            } else if ("check".equalsIgnoreCase(args[0])) {
                final long lastRefreshTime = StatusCheckRunner.getLastRefreshTime();
                final BotStatus status = StatusCheckRunner.getStatus();
                sender.sendMessage(String.format(
                        "在线状态: %s\n上次更新: %s",
                        status == null ? "失去连接" : status.getOnline(),
                        FORMAT.format(lastRefreshTime)
                ));
            } else if ("reload".equalsIgnoreCase(args[0])) {
                BotConfiguration.reload();
                sender.sendMessage("config.yml 部分配置节点重载完成");
            }
            return true;
        }
        if (args.length != 2) return true;
        final ExpansionLoader loader = Bootstrap.getInstance().getExpansionLoader();
        if ("expansions".equalsIgnoreCase(args[0])) {
            if ("list".equalsIgnoreCase(args[1])) {
                sender.sendMessage(loader.getExpansionNames().toString());
            } else if ("reload".equalsIgnoreCase(args[1])) {
                Bukkit.getScheduler().runTaskAsynchronously(Bootstrap.getInstance(), () -> {
                    loader.disableExpansions();
                    loader.loadExpansions();
                });
            }
        } else if ("load".equalsIgnoreCase(args[0])) {
            File file = new File(loader.getPluginFolder(), args[1]);
            if (!file.exists() || file.isDirectory()) {
                sender.sendMessage("文件类型错误");
                return true;
            }
            loader.loadExpansion(file);
        } else if ("unload".equalsIgnoreCase(args[0])) {
            final Optional<String> first = loader.getExpansionNames().stream().filter(name -> name.equals(args[1])).findFirst();
            if (!first.isPresent()) {
                sender.sendMessage("附属名称 " + args[0] + " 不存在");
                return true;
            }
            loader.disableExpansion(first.get());
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) return null;
        List<String> result = new ArrayList<>();
        if (args.length == 1)
            result.addAll(Arrays.asList("help", "check", "reload", "expansions", "load", "unload"));
        else if (args.length == 2) {
            switch (args[0]) {
                case "expansions": {
                    result.add("list");
                    result.add("reload");
                    break;
                }
                case "unload": {
                    final ExpansionLoader loader = Bootstrap.getInstance().getExpansionLoader();
                    result.addAll(loader.getExpansionNames());
                    break;
                }
                case "load": {
                    final ExpansionLoader loader = Bootstrap.getInstance().getExpansionLoader();
                    File[] files = loader.getPluginFolder().listFiles((dir, name) -> name.endsWith(".jar"));
                    if (files != null) {
                        final List<String> collect = Arrays.stream(files)
                                .filter(file -> !file.isDirectory())
                                .map(File::getName)
                                .collect(Collectors.toList());
                        result.addAll(collect);
                    }
                    break;
                }
            }
        }
        return result;
    }

}
