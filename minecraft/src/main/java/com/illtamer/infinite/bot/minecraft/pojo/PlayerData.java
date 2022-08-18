package com.illtamer.infinite.bot.minecraft.pojo;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class PlayerData {

    /**
     * Use for database
     * */
    private Integer id;

    /**
     * Minecraft UUID
     * */
    private String uuid;

    /**
     * Valid UUID by MoJang
     * */
    private String validUUID;

    /**
     * qq 号
     * */
    private Long userId;

    private List<String> permission;

    /**
     * 获取倾向的首个不为空的 uuid
     * <p>
     * TODO 倾向可配置
     * */
    @Nullable
    public String getPreferUUID() {
        return uuid == null ? validUUID : uuid;
    }

    @Nullable
    public OfflinePlayer getValidOfflinePlayer() {
        return validUUID != null ? Bukkit.getOfflinePlayer(UUID.fromString(validUUID)) : null;
    }

    @Nullable
    public OfflinePlayer getOfflinePlayer() {
        return uuid != null ? Bukkit.getOfflinePlayer(UUID.fromString(uuid)) : null;
    }

    /**
     * Use for yaml
     * */
    public Map<String, Object> toMap() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>(3);
        if (uuid != null)
            map.put("uuid", uuid);
        if (validUUID != null)
            map.put("user_name", validUUID);
        if (userId != null)
            map.put("user_id", userId);
        if (permission != null && permission.size() != 0)
            map.put("permission", permission);
        return map;
    }

}
