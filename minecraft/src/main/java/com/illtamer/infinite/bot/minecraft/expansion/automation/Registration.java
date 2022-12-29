package com.illtamer.infinite.bot.minecraft.expansion.automation;

import com.illtamer.infinite.bot.minecraft.api.IExpansion;
import com.illtamer.infinite.bot.minecraft.exception.InitializationException;
import com.illtamer.infinite.bot.minecraft.expansion.ExpansionConfig;
import com.illtamer.infinite.bot.minecraft.expansion.automation.factory.SingletonFactory;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Enum 字段在注册前应重写 #toString() 和 #valueOf(), 以便于正确进行序列化和反序列化
 * */
public class Registration {

    private static final Map<IExpansion, Set<Class<? extends AutoLoadConfiguration>>> autoConfigMap = new HashMap<>();

    /**
     * 注册自动配置类
     * @apiNote 请在 onEnable 方法中调用
     * */
    @SneakyThrows
    public static void add(AutoLoadConfiguration instance, IExpansion expansion) {
        final Class<? extends AutoLoadConfiguration> clazz = instance.getClass();
        final Set<Class<? extends AutoLoadConfiguration>> set = autoConfigMap.computeIfAbsent(expansion, k -> new HashSet<>());
        if (set.contains(clazz))
            throw new IllegalArgumentException("Duplicate auto-config class: " + clazz);
        set.add(clazz);
        ConfigurationSerialization.registerClass(clazz);
        SingletonFactory.setInstance(clazz, instance);
    }

    /**
     * 获取自动配置类单例
     * */
    public static <T extends AutoLoadConfiguration> T get(Class<T> clazz) {
        try {
            return SingletonFactory.getInstance(clazz);
        } catch (InitializationException e) {
            throw new IllegalArgumentException("未注册的自动配置类: " + clazz);
        }
    }

    /**
     * 注销并保存所有自动配置文件
     * */
    public static void removeAndStoreAutoConfigs(IExpansion expansion) {
        final Set<Class<? extends AutoLoadConfiguration>> set = autoConfigMap.remove(expansion);
        if (set == null || set.size() == 0) return;
        for (Class<? extends AutoLoadConfiguration> clazz : set) {
            final AutoLoadConfiguration configuration = SingletonFactory.remove(clazz);
            final ExpansionConfig configFile = configuration.getConfigFile();
            final FileConfiguration config = configFile.getConfig();
            for (Map.Entry<String, Object> entry : configuration.serialize().entrySet()) {
                config.set(entry.getKey(), entry.getValue());
            }
            configFile.save();
            ConfigurationSerialization.unregisterClass(clazz);
        }
    }

}
