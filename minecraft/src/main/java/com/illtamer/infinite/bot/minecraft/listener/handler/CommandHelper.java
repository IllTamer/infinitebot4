package com.illtamer.infinite.bot.minecraft.listener.handler;

import com.illtamer.infinite.bot.minecraft.api.BotScheduler;
import com.illtamer.infinite.bot.minecraft.api.IExpansion;
import com.illtamer.infinite.bot.minecraft.api.IExternalExpansion;
import com.illtamer.infinite.bot.minecraft.api.StaticAPI;
import com.illtamer.infinite.bot.minecraft.api.adapter.Bootstrap;
import com.illtamer.infinite.bot.minecraft.api.adapter.CommandSender;
import com.illtamer.infinite.bot.minecraft.configuration.StatusCheckRunner;
import com.illtamer.infinite.bot.minecraft.configuration.config.BotConfiguration;
import com.illtamer.infinite.bot.minecraft.expansion.ExpansionLoader;
import com.illtamer.infinite.bot.minecraft.pojo.PlayerData;
import com.illtamer.infinite.bot.minecraft.start.bukkit.BukkitBootstrap;
import com.illtamer.infinite.bot.minecraft.util.BukkitUtil;
import com.illtamer.infinite.bot.minecraft.util.StringUtil;
import com.illtamer.perpetua.sdk.entity.transfer.entity.Client;
import com.illtamer.perpetua.sdk.entity.transfer.entity.LoginInfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandHelper {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm:ss");

    public static boolean onCommand(CommandSender sender, String[] args, Bootstrap.Type type) {
        if (!sender.isOp()) return true;
        if (args.length == 0) return true;

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "debug": {
                if (args.length == 1) {
                    System.out.println(BotConfiguration.connection);
                }
                return true;
            }
            case "help": {
                sendHelp(sender);
                return true;
            }
            case "check": {
                if (args.length == 1) {
                    final long lastRefreshTime = StatusCheckRunner.getLastRefreshTime();
                    final LoginInfo loginInfo = StatusCheckRunner.getLoginInfo();
                    if (loginInfo == null) {
                        sender.sendMessage("登陆数据刷新中，请稍后再试");
                        return true;
                    }
                    Client client = StaticAPI.getClient();
                    sender.sendMessage(String.format(
                            "当前登陆账号: %d(%s)\nClient: %s(id: %s)\n上次更新: %s",
                            loginInfo.getUserId(), loginInfo.getNickname(),
                            StringUtil.isBlank(client.getClientName()) ? "[未设置别名]" : client.getClientName(), client.getAppId(),
                            FORMAT.format(lastRefreshTime)
                    ));
                }
                return true;
            }
            case "reload": {
                if (args.length == 1) {
                    BotConfiguration.reload();
                    sender.sendMessage("config.yml 部分配置节点重载完成");
                }
                return true;
            }
            case "reconnect": {
                if (args.length != 1) return true;
                if (!ensureBukkitEnv(sender, type)) return true;
                StaticAPI.reconnected();
                sender.sendMessage("已触发重连任务，请稍后使用 /ib4 check 查看连接状态");
                return true;
            }
            case "query": {
                if (!ensureBukkitEnv(sender, type)) return true;
                if (args.length != 3) {
                    sender.sendMessage(new String[] {
                            "query 指令用法:",
                            " ├──/ib4 query qq <qq号>",
                            " └──/ib4 query name <玩家名>"
                    });
                    return true;
                }

                BotScheduler.runTask(() -> {
                    String queryType = args[1].toLowerCase();
                    if ("qq".equals(queryType)) {
                        final long userId;
                        try {
                            userId = Long.parseLong(args[2]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage("QQ 号格式错误: " + args[2]);
                            return;
                        }

                        PlayerData data = StaticAPI.getRepository().queryByUserId(userId);
                        if (data == null) {
                            sender.sendMessage("未找到 QQ " + userId + " 对应的玩家绑定信息");
                            return;
                        }

                        String playerName = BukkitUtil.getPlayerName(data.getUuid(), data.getValidUUID());
                        sendPlayerData(sender, userId, playerName);
                    } else if ("name".equals(queryType)) {
                        String playerName = args[2];
                        PlayerData data = StaticAPI.getRepository().queryByUUID(BukkitUtil.getPlayerUUID(playerName));
                        if (data == null) {
                            sender.sendMessage("玩家 " + playerName + " 暂无绑定信息");
                            return;
                        }

                        sendPlayerData(sender, data.getUserId(), playerName);
                    } else {
                        sender.sendMessage("未知的查询类型: " + args[1] + "，可选 qq/name");
                    }
                });
                return true;
            }
            case "expansions": {
                if (!ensureBukkitEnv(sender, type)) return true;
                if (args.length != 2) return true;
                final ExpansionLoader loader = BukkitBootstrap.getInstance().getExpansionLoader();
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
                return true;
            }
            case "load": {
                if (!ensureBukkitEnv(sender, type)) return true;
                if (args.length != 2) {
                    sender.sendMessage("用法: /ib4 load <附属文件名>");
                    return true;
                }
                final ExpansionLoader loader = BukkitBootstrap.getInstance().getExpansionLoader();
                File file = new File(loader.getPluginFolder(), args[1]);
                if (!file.exists() || file.isDirectory()) {
                    sender.sendMessage("文件类型错误");
                    return true;
                }
                loader.loadExpansion(file);
                return true;
            }
            case "unload": {
                if (!ensureBukkitEnv(sender, type)) return true;
                if (args.length != 2) {
                    sender.sendMessage("用法: /ib4 unload <附属标识符>");
                    return true;
                }
                final ExpansionLoader loader = BukkitBootstrap.getInstance().getExpansionLoader();
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
                return true;
            }
            default:
                return true;
        }
    }

    private static void sendHelp(CommandSender sender) {
        sender.sendMessage(new String[] {
                "ib4:",
                " ├──help: 获取帮助",
                " ├──check: 检查账号的连接状态",
                " ├──reload: 重载本体配置文件",
                " ├──reconnect: 手动触发连接重建",
                " ├──query",
                " │   ├──qq [qq号]: 通过 qq 查询玩家绑定信息",
                " │   └──name [玩家名]: 通过玩家名查询玩家绑定信息（依赖本服缓存）",
                " ├──expansions",
                " │   ├──list: 列出所有加载的附属名称",
                " │   └──reload: 重载 expansions 目录下所有附属",
                " ├──load",
                " │   └──[附属文件名]: 加载名称对应附属",
                " └──unload",
                "     └──[附属名称]: 卸载名称对应附属"
        });
    }

    private static boolean ensureBukkitEnv(CommandSender sender, Bootstrap.Type type) {
        if (type == Bootstrap.Type.BUKKIT) {
            return true;
        }
        sender.sendMessage("当前非 BUKKIT 环境，部分指令已禁用");
        return false;
    }

    private static void sendPlayerData(CommandSender sender, Long qq, String name) {
        sender.sendMessage(new String[] {
                "玩家绑定信息:",
                " ├──玩家名: " + name,
                " └──QQ: " + qq
        });
    }


    public static List<String> onTabComplete(CommandSender sender, String[] args) {
        if (!sender.isOp()) return null;
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            result.addAll(Arrays.asList("help", "check", "reload", "reconnect", "query", "expansions", "load", "unload"));
        } else if (args.length == 2) {
            switch (args[0]) {
                case "expansions": {
                    result.add("list");
                    result.add("reload");
                    break;
                }
                case "query": {
                    result.add("qq");
                    result.add("name");
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
        } else if (args.length == 3) {
            if ("query".equals(args[0]) && "name".equals(args[1])) {
                result.addAll(BukkitUtil.onlinePlayerNameList());
            } else if ("query".equals(args[0]) && "qq".equals(args[1])) {
                result.add("<qq>");
            }
        }
        return result;
    }

}
