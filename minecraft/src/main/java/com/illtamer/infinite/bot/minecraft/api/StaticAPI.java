package com.illtamer.infinite.bot.minecraft.api;

import com.illtamer.infinite.bot.api.handler.OpenAPIHandling;
import com.illtamer.infinite.bot.api.message.Message;
import com.illtamer.infinite.bot.minecraft.Bootstrap;
import com.illtamer.infinite.bot.minecraft.configuration.config.BotConfiguration;
import com.illtamer.infinite.bot.minecraft.repository.PlayerDataRepository;

public class StaticAPI {

    public static boolean isAdmin(long userId) {
        return BotConfiguration.main.admins.contains(userId);
    }

    public static boolean inGroups(long groupId) {
        return BotConfiguration.main.groups.contains(groupId);
    }

    /**
     * 发送私人消息
     * @return 消息 ID
     * */
    public static double sendMessage(String message, long userId) {
        return OpenAPIHandling.sendMessage(message, userId);
    }

    /**
     * 发送私人消息
     * @return 消息 ID
     * */
    public static double sendMessage(Message message, long userId) {
        return OpenAPIHandling.sendMessage(message, userId);
    }

    /**
     * 向群组发送消息
     * @return 消息 ID
     * */
    public static double sendGroupMessage(String message, long groupId) {
        return OpenAPIHandling.sendGroupMessage(message, groupId);
    }

    /**
     * 向群组发送消息
     * @return 消息 ID
     * */
    public static double sendGroupMessage(Message message, long groupId) {
        return OpenAPIHandling.sendGroupMessage(message, groupId);
    }

    /**
     * 重连 go-cqhttp WebSocket 服务
     * */
    public static void reconnected() {
        Bootstrap.getInstance().connect();
    }

    /**
     * 查询是否存在指定名称的附属
     * @param name 附属名，若注册时未指定则为启动类的全限定名称
     * */
    public static boolean hasExpansion(String name) {
        return Bootstrap.getInstance().getExpansionLoader().getExpansion(name) != null;
    }

    /**
     * 获取 PlayerData 持久层对象
     * */
    public static PlayerDataRepository getRepository() {
        return BotConfiguration.getInstance().getRepository();
    }

}
