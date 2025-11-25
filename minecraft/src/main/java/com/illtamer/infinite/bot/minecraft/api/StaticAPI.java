package com.illtamer.infinite.bot.minecraft.api;

import com.illtamer.infinite.bot.minecraft.api.adapter.Bootstrap;
import com.illtamer.infinite.bot.minecraft.configuration.BotNettyHolder;
import com.illtamer.infinite.bot.minecraft.configuration.config.BotConfiguration;
import com.illtamer.infinite.bot.minecraft.expansion.ExpansionLoader;
import com.illtamer.infinite.bot.minecraft.pojo.ExpansionIdentifier;
import com.illtamer.infinite.bot.minecraft.repository.PlayerDataRepository;
import com.illtamer.infinite.bot.minecraft.start.bukkit.BukkitBootstrap;
import com.illtamer.infinite.bot.minecraft.util.ExpansionUtil;
import com.illtamer.perpetua.sdk.entity.transfer.entity.Client;
import com.illtamer.perpetua.sdk.util.Assert;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StaticAPI {

    /**
     * -- GETTER --
     *  获取当前启动器实例
     *
     */
    @Setter
    @Getter
    private static Bootstrap instance;

    @Getter
    private static final Client client = new Client();

    public static boolean isAdmin(long userId) {
        return BotConfiguration.main.admins.contains(userId);
    }

    public static boolean inGroups(long groupId) {
        return BotConfiguration.main.groups.contains(groupId);
    }

    /**
     * 重连 perpetua WebSocket 服务
     * */
    public static void reconnected() {
        BukkitBootstrap inst = BukkitBootstrap.getInstance();
        Assert.notNull(inst, "Bukkit plugin instance is null, is there a bukkit mode ?");
        inst.getNettyHolder().ifPresent(BotNettyHolder::connect);
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

}
