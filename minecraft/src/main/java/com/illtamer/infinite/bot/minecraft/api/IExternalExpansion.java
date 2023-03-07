package com.illtamer.infinite.bot.minecraft.api;

import com.illtamer.infinite.bot.minecraft.start.bukkit.BukkitBootstrap;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Infinite Bot 外部扩展附属接口
 * */
public interface IExternalExpansion extends IExpansion {

    /**
     * 主动注册附属 TODO 测试位于附属目录下能否正常使用，能否检测不正确加载
     * @apiNote 主动注册时，附属不可位于附属目录下，否则将被重复注册抛出异常
     * */
    void register(@NotNull Plugin plugin);

    /**
     * 主动卸载附属
     * */
    void unregister();

    /**
     * 附属是否已被注册
     * */
    boolean isRegister();

    /**
     * 是否允许注册
     * */
    default boolean canRegister() {
        return true;
    }

    /**
     * 是否为持久化实例
     * <p>
     * 当此方法返回为 true 时，指令将不会卸载实例（其余途径如注册插件自身被卸载、IB3被卸载等仍会触发卸载）
     * */
    default boolean isPersist() {
        return true;
    }

    /**
     * 获取插件本体实例
     * */
    default BukkitBootstrap getPluginInstance() {
        return BukkitBootstrap.getInstance();
    }

}
