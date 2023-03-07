package com.illtamer.infinite.bot.minecraft.api;

import com.illtamer.infinite.bot.api.handler.OpenAPIHandling;
import com.illtamer.infinite.bot.api.message.Message;
import com.illtamer.infinite.bot.api.util.Assert;
import com.illtamer.infinite.bot.minecraft.api.adapter.Bootstrap;
import com.illtamer.infinite.bot.minecraft.start.bukkit.BukkitBootstrap;
import com.illtamer.infinite.bot.minecraft.configuration.config.BotConfiguration;
import com.illtamer.infinite.bot.minecraft.expansion.ExpansionLoader;
import com.illtamer.infinite.bot.minecraft.pojo.ExpansionIdentifier;
import com.illtamer.infinite.bot.minecraft.repository.PlayerDataRepository;
import com.illtamer.infinite.bot.minecraft.util.ExpansionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StaticAPI {

    private static Bootstrap instance;

    public static boolean isAdmin(long userId) {
        return BotConfiguration.main.admins.contains(userId);
    }

    public static boolean inGroups(long groupId) {
        return BotConfiguration.main.groups.contains(groupId);
    }

    /**
     * 发送私人消息
     * @return 消息 ID
     * @deprecated see {@link OpenAPIHandling#sendMessage(String, long)}
     * */
    @Deprecated
    public static double sendMessage(String message, long userId) {
        return OpenAPIHandling.sendMessage(message, userId);
    }

    /**
     * 发送私人消息
     * @return 消息 ID
     * @deprecated see {@link OpenAPIHandling#sendMessage(Message, long)}
     * */
    @Deprecated
    public static double sendMessage(Message message, long userId) {
        return OpenAPIHandling.sendMessage(message, userId);
    }

    /**
     * 向群组发送消息
     * @return 消息 ID
     * @deprecated see {@link OpenAPIHandling#sendGroupMessage(String, long)}
     * */
    @Deprecated
    public static double sendGroupMessage(String message, long groupId) {
        return OpenAPIHandling.sendGroupMessage(message, groupId);
    }

    /**
     * 向群组发送消息
     * @return 消息 ID
     * @deprecated see {@link OpenAPIHandling#sendGroupMessage(Message, long)}
     * */
    @Deprecated
    public static double sendGroupMessage(Message message, long groupId) {
        return OpenAPIHandling.sendGroupMessage(message, groupId);
    }

    /**
     * 重连 go-cqhttp WebSocket 服务
     * */
    public static void reconnected() {
        BukkitBootstrap.getInstance().connect();
    }

    /**
     * 查询是否存在指定名称的附属
     * @param name 附属名，若注册时未指定则为启动类的全限定名称
     * */
    @Deprecated
    public static boolean hasExpansion(String name) {
        return false;
    }

    /**
     * 查询是否存在指定名称与作者的附属
     * @param name 附属名，若注册时未指定则为启动类的全限定名称
     * @param author 作者名称
     * */
    public static boolean hasExpansion(@NotNull String name, @NotNull String author) {
        return getExpansion(name, author) != null;
    }

    /**
     * 查询是否存在指定名称、作者与版本的附属
     * @param name 附属名，若注册时未指定则为启动类的全限定名称
     * @param version 指定版本
     * @param author 作者名称
     * */
    public static boolean hasExpansion(@NotNull String name, @NotNull String version, @NotNull String author) {
        return getExpansion(name, version, author) != null;
    }

    /**
     * 获取指定名称与作者的附属
     * @param name 附属名，若注册时未指定则为启动类的全限定名称
     * @param author 作者名称
     */
    @Nullable
    public static IExpansion getExpansion(@NotNull String name, @NotNull String author) {
        final ExpansionLoader loader = BukkitBootstrap.getInstance().getExpansionLoader();
        for (String identifier : loader.getExpansionKeySet()){
            if (!identifier.startsWith(name)) continue;
            final ExpansionIdentifier ei = ExpansionUtil.parseIdentifier(identifier);
            Assert.notNull(ei, "Unauthenticated identifier: " + identifier);
            if (ei.getName().equals(name) && ei.getAuthor().equals(author))
                return loader.getExpansion(identifier);
        }
        return null;
    }

    /**
     * 获取指定名称、作者与版本的附属
     * @param name 附属名，若注册时未指定则为启动类的全限定名称
     * @param version 指定版本
     * @param author 作者名称
     * */
    public static IExpansion getExpansion(@NotNull String name, @NotNull String version, @NotNull String author) {
        return BukkitBootstrap.getInstance().getExpansionLoader().getExpansion(ExpansionUtil.formatIdentifier(name, version, author));
    }

    /**
     * 获取 PlayerData 持久层实例
     * */
    public static PlayerDataRepository getRepository() {
        return BotConfiguration.getInstance().getRepository();
    }

    /**
     * 设置当前启动器实例
     * @apiNote 启动器启动后自动调用
     * */
    public static void setInstance(Bootstrap instance) {
        StaticAPI.instance = instance;
    }

    /**
     * 获取当前启动器实例
     * */
    public static Bootstrap getInstance() {
        return instance;
    }

}
