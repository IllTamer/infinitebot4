package com.illtamer.infinite.bot.minecraft.api.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * 附属声明式命令定义
 * <p>
 * 指令由 InfiniteBot4 本体在附属启用时注册到 Bukkit，在附属卸载时自动反注册。
 * */
public abstract class ExpansionCommand {

    /**
     * 主命令名
     * */
    @NotNull
    public abstract String getName();

    /**
     * 命令别名
     * */
    @NotNull
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    /**
     * 命令描述
     * */
    @NotNull
    public String getDescription() {
        return "";
    }

    /**
     * 命令用法
     * */
    @NotNull
    public String getUsage() {
        return '/' + getName();
    }

    /**
     * 命令权限节点
     * @return null 表示不校验权限
     * */
    @Nullable
    public String getPermission() {
        return null;
    }

    /**
     * 无权限提示
     * @return null 表示使用 Bukkit 默认提示
     * */
    @Nullable
    public String getPermissionMessage() {
        return null;
    }

    /**
     * 执行命令
     * */
    public abstract boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args);

    /**
     * Tab 补全
     * @return null 表示无补全
     * */
    @Nullable
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return null;
    }

}
