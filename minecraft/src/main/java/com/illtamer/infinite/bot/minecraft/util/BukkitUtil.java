package com.illtamer.infinite.bot.minecraft.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BukkitUtil {

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

}
