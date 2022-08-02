package com.illtamer.infinite.bot.minecraft.pojo;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

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
     * qq 号
     * */
    private Long userId;

    private List<String> permission;

    @NotNull
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(UUID.fromString(uuid));
    }

    /**
     * Use for yaml
     * */
    public Map<String, Object> toMap() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>(3);
        if (uuid != null)
            map.put("uuid", uuid);
        if (userId != null)
            map.put("user_id", userId);
        if (permission != null && permission.size() != 0)
            map.put("permission", permission);
        return map;
    }

}
