package com.illtamer.infinite.bot.minecraft.util;

import com.illtamer.infinite.bot.minecraft.start.bukkit.BukkitBootstrap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public final class BukkitUtil {

    public static UUID getPlayerUUID(String name) {
        return Bukkit.getOfflinePlayer(name).getUniqueId();
    }

    @Nullable
    public static String getPlayerName(@Nullable String uuid, @Nullable String validUUID) {
        if (StringUtil.isBlank(uuid) && StringUtil.isBlank(validUUID)) {
            return null;
        }
        return StringUtil.isBlank(uuid) ?
                Bukkit.getOfflinePlayer(UUID.fromString(validUUID)).getName() :
                Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
    }

    public static List<String> onlinePlayerNameList() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

    public static <T> T callInMainThread(@NotNull Callable<T> action) {
        if (Bukkit.isPrimaryThread()) {
            try {
                return action.call();
            } catch (Exception e) {
                throw new IllegalStateException("Failed to execute command-map operation", e);
            }
        }

        final BukkitBootstrap instance = BukkitBootstrap.getInstance();
        if (instance == null) {
            throw new IllegalStateException("BukkitBootstrap instance is null");
        }

        try {
            return Bukkit.getScheduler().callSyncMethod(instance, action).get();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to switch to Bukkit main thread for command-map operation", e);
        }
    }

}
