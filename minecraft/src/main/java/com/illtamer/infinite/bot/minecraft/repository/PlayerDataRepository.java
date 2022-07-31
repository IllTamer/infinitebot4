package com.illtamer.infinite.bot.minecraft.repository;

import com.illtamer.infinite.bot.minecraft.pojo.PlayerData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface PlayerDataRepository {

    boolean save(@NotNull PlayerData data);

    @Nullable
    PlayerData queryByUUID(@NotNull UUID uuid);

    @Nullable
    PlayerData queryByUUID(@NotNull String uuid);

    @Nullable
    PlayerData queryByUserId(@NotNull Long userId);

    /**
     * 更新玩家数据
     * @return 更新前的玩家数据
     * */
    @Nullable
    PlayerData update(@NotNull PlayerData data);

    /**
     * 删除玩家数据
     * @return 移除前的玩家数据
     * */
    @Nullable
    PlayerData delete(@NotNull String uuid);

    /**
     * 删除玩家数据
     * @return 移除前的玩家数据
     * */
    @Nullable
    PlayerData delete(@NotNull Long userId);

    /**
     * 将缓存数据写入硬盘
     * */
    void saveCacheData();

}
