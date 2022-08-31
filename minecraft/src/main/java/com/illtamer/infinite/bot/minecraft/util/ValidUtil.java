package com.illtamer.infinite.bot.minecraft.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.illtamer.infinite.bot.api.Pair;
import com.illtamer.infinite.bot.api.util.HttpRequestUtil;
import com.illtamer.infinite.bot.minecraft.Bootstrap;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@UtilityClass
public class ValidUtil {
    private static final String SESSION_SERVER = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final Gson GSON = new Gson();

    public static boolean isValidPlayer(@NotNull Player player) {
        return isValid(player.getUniqueId(), player.getName());
    }

    public static boolean isValid(@NotNull UUID uuid, @NotNull String name) {
        return isValid(uuid.toString(), name);
    }

    public static boolean isValid(@NotNull String uuid, @NotNull String name) {
        try {
            final Pair<Integer, String> pair = HttpRequestUtil.getJson(SESSION_SERVER + uuid, null);
            final Integer status = pair.getKey();
            if (status == 400) return false;
            if (status != 200) {
                Bootstrap.getInstance().getLogger().warning("验证服务器意思不可用，状态码: " + status);
                return false;
            }
            final JsonObject object = GSON.fromJson(pair.getValue(), JsonObject.class);
            return name.equals(object.get("name").getAsString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isValidUUID(@NotNull UUID uuid) {
        return isValidUUID(uuid.toString());
    }

    public static boolean isValidUUID(@NotNull String uuid) {
        try {
            final Pair<Integer, String> pair = HttpRequestUtil.getJson(SESSION_SERVER + uuid, null);
            final Integer status = pair.getKey();
            if (status == 200) return true;
            Bootstrap.getInstance().getLogger().warning("验证服务器意思不可用，状态码: " + status);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

}
