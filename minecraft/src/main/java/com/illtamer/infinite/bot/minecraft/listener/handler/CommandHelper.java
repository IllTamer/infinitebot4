package com.illtamer.infinite.bot.minecraft.listener.handler;

import com.illtamer.infinite.bot.minecraft.api.BotScheduler;
import com.illtamer.infinite.bot.minecraft.api.IExpansion;
import com.illtamer.infinite.bot.minecraft.api.IExternalExpansion;
import com.illtamer.infinite.bot.minecraft.api.adapter.Bootstrap;
import com.illtamer.infinite.bot.minecraft.api.adapter.CommandSender;
import com.illtamer.infinite.bot.minecraft.configuration.StatusCheckRunner;
import com.illtamer.infinite.bot.minecraft.configuration.config.BotConfiguration;
import com.illtamer.infinite.bot.minecraft.expansion.ExpansionLoader;
import com.illtamer.infinite.bot.minecraft.start.bukkit.BukkitBootstrap;
import com.illtamer.perpetua.sdk.entity.transfer.entity.LoginInfo;
import com.illtamer.perpetua.sdk.entity.transfer.entity.Status;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandHelper {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm:ss");

    public static boolean onCommand(CommandSender sender, String[] args, Bootstrap.Type type) {
        if (!sender.isOp()) return true;
        if (args.length == 1) {
            if ("debug".equals(args[0])) {
                System.out.println(BotConfiguration.connection);
                return true;
            }
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
                final LoginInfo loginInfo = StatusCheckRunner.getLoginInfo();
                if (loginInfo == null) {
                    sender.sendMessage("登陆数据刷新中，请稍后再试");
                    return true;
                }
                sender.sendMessage(String.format(
                        "当前登陆账号: %d(%s)\n上次更新: %s",
                        loginInfo.getUserId(), loginInfo.getNickname(),
                        FORMAT.format(lastRefreshTime)
                ));
            } else if ("reload".equalsIgnoreCase(args[0])) {
                BotConfiguration.reload();
                sender.sendMessage("config.yml 部分配置节点重载完成");
            }
            return true;
        }
        if (args.length != 2) return true;
        if (type != Bootstrap.Type.BUKKIT) {
            sender.sendMessage("当前非 BUKKIT 环境，部分指令已禁用");
            return true;
        }
        final ExpansionLoader loader = BukkitBootstrap.getInstance().getExpansionLoader();
        if ("expansions".equalsIgnoreCase(args[0])) {
            if ("list".equalsIgnoreCase(args[1])) {
                final Set<String> set = loader.getExpansionKeySet();
                String[] keyArray = new String[set.size()];
                sender.sendMessage(set.toArray(keyArray));
            } else if ("reload".equalsIgnoreCase(args[1])) {
                BotScheduler.runTask(() -> {
                    loader.disableExpansions(true);
                    loader.loadExpansions(true);
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
            final IExpansion expansion = loader.getExpansion(args[1]);
            if (expansion == null) {
                sender.sendMessage("附属标识符 " + args[1] + " 不存在");
                return true;
            }
            if (expansion instanceof IExternalExpansion && ((IExternalExpansion) expansion).isPersist()) {
                sender.sendMessage("该附属为持久化附属，不可通过指令卸载!");
                return true;
            }
            loader.disableExpansion(args[1]);
        }
        return true;
    }

    public static List<String> onTabComplete(CommandSender sender, String[] args) {
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
                    final ExpansionLoader loader = BukkitBootstrap.getInstance().getExpansionLoader();
                    result.addAll(loader.getExpansionKeySet());
                    break;
                }
                case "load": {
                    final ExpansionLoader loader = BukkitBootstrap.getInstance().getExpansionLoader();
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
