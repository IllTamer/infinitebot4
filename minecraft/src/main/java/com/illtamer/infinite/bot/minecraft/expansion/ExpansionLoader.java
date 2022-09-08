package com.illtamer.infinite.bot.minecraft.expansion;

import com.illtamer.infinite.bot.api.util.Assert;
import com.illtamer.infinite.bot.minecraft.Bootstrap;
import com.illtamer.infinite.bot.minecraft.api.IExpansion;
import com.illtamer.infinite.bot.minecraft.api.IExternalExpansion;
import com.illtamer.infinite.bot.minecraft.exception.InvalidExpansionException;
import com.illtamer.infinite.bot.minecraft.expansion.manager.InfinitePluginLoader;
import com.illtamer.infinite.bot.minecraft.util.ExpansionUtil;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 附属总加载类
 * */
public class ExpansionLoader {
    private static final InfinitePluginLoader PLUGIN_LOADER = new InfinitePluginLoader();
    private static final Map<String, IExpansion> EXPANSION_MAP = new ConcurrentHashMap<>();
    private static final Map<IExternalExpansion, Plugin> EXTERNAL_EXPANSION_MAP = new ConcurrentHashMap<>();
    private final Bootstrap instance;
    private final Logger log;
    private final File pluginFolder;

    public ExpansionLoader(Bootstrap instance) {
        this.instance = instance;
        this.log = instance.getLogger();
        this.pluginFolder = new File(instance.getDataFolder(), "expansions");
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }
    }

    /**
     * 加载并初始化附属
     * */
    public void loadExpansion(File file) {
        if (file.isDirectory() || !file.getName().endsWith(".jar")) {
            log.warning("File " + file.getName() + " is not a valid jar file!");
            return;
        }
        IExpansion expansion = null;
        try {
            expansion = PLUGIN_LOADER.loadExpansion(file);
            EXPANSION_MAP.put(expansion.toString(), expansion);
            PLUGIN_LOADER.enableExpansion(expansion);
        } catch (InvalidExpansionException e) {
            e.printStackTrace();
        }
        Assert.isTrue(expansion != null && expansion.isEnabled(), String.format("附属 %s 异常加载!", file.getName()));
    }

    /**
     * 加载并初始化外部附属
     * */
    public void loadExternalExpansion(IExternalExpansion externalExpansion, Plugin plugin) {
        if (!externalExpansion.canRegister()) {
            externalExpansion.getLogger().info("Registration conditions are not met!");
            return;
        }
        if (EXTERNAL_EXPANSION_MAP.containsKey(externalExpansion)) {
            log.warning("ExternalExpansion " + ExpansionUtil.formatIdentifier(externalExpansion) + " has been repeatedly registered with plugin: " + plugin.getName());
            return;
        }
        EXTERNAL_EXPANSION_MAP.put(externalExpansion, plugin);
        EXPANSION_MAP.put(externalExpansion.toString(), externalExpansion);
        PLUGIN_LOADER.enableExternalExpansion(externalExpansion);
        Assert.isTrue(externalExpansion.isEnabled(), String.format("附属 %s 异常加载!", ExpansionUtil.formatIdentifier(externalExpansion)));
    }

    /**
     * 加载并初始化所有插件
     * @param persist 是否考虑持久化附属
     * */
    public void loadExpansions(boolean persist) {
        if (EXPANSION_MAP.size() != 0) {
            disableExpansions(persist);
        }
        File[] expansions = pluginFolder.listFiles((dir, name) -> name.endsWith(".jar"));
        if (expansions == null || expansions.length == 0) {
            return;
        }
        int available = 0;
        for (File expansion : expansions) {
            if (expansion.isDirectory()) continue;
            try {
                final IExpansion loadExpansion = PLUGIN_LOADER.loadExpansion(expansion);
                EXPANSION_MAP.put(loadExpansion.toString(), loadExpansion);
                ++ available;
            } catch (InvalidExpansionException e) {
                e.printStackTrace();
            }
        }
        log.info(String.format("检测到 %d 个内部附属, 正在初始化...", available));
        for (IExpansion expansion : EXPANSION_MAP.values()) {
            if (expansion instanceof IExternalExpansion && ((IExternalExpansion) expansion).isPersist()) continue;
            Assert.isTrue(!expansion.isEnabled(), String.format("附属 %s 异常加载!", ExpansionUtil.formatIdentifier(expansion)));
            PLUGIN_LOADER.enableExpansion(expansion);
        }
    }

    /**
     * 卸载指定附属
     * */
    public boolean disableExpansion(String identifier) {
        final IExpansion expansion = EXPANSION_MAP.remove(identifier);
        if (expansion == null) {
            log.warning("指定附属 " + identifier + " 不存在!");
            return false;
        }
        if (expansion instanceof IExternalExpansion)
            return disableExternalExpansion((IExternalExpansion) expansion);
        PLUGIN_LOADER.disableExpansion(expansion);
        return true;
    }

    /**
     * 卸载外部附属
     * */
    public boolean disableExternalExpansion(IExternalExpansion externalExpansion) {
        if (!EXTERNAL_EXPANSION_MAP.containsKey(externalExpansion)) {
            log.warning("ExternalExpansion " + ExpansionUtil.formatIdentifier(externalExpansion) + " has not been loaded!");
            return false;
        }
        PLUGIN_LOADER.disableExternalExpansion(externalExpansion);
        EXPANSION_MAP.remove(externalExpansion.toString());
        EXTERNAL_EXPANSION_MAP.remove(externalExpansion);
        return true;
    }

    /**
     * 卸载所有插件并调用其所有onDisable
     * @param persist 是否考虑持久化附属
     * */
    public void disableExpansions(boolean persist) {
        for (Map.Entry<String, IExpansion> entry : EXPANSION_MAP.entrySet()) {
            final IExpansion expansion = entry.getValue();
            if (persist && expansion instanceof IExternalExpansion && ((IExternalExpansion) expansion).isPersist()) {
                log.info("持久化外部附属 " + entry.getKey() + " 跳过卸载");
                continue;
            }
            if (expansion instanceof IExternalExpansion)
                PLUGIN_LOADER.disableExternalExpansion((IExternalExpansion) expansion);
            else
                PLUGIN_LOADER.disableExpansion(expansion);
            EXPANSION_MAP.remove(entry.getKey());
        }
        System.gc();
    }

    @Nullable
    public IExpansion getExpansion(String identifier) {
        return EXPANSION_MAP.get(identifier);
    }

    @Nullable
    public IExternalExpansion getExternalExpansion(Plugin plugin) {
        for (Map.Entry<IExternalExpansion, Plugin> entry : EXTERNAL_EXPANSION_MAP.entrySet())
            if (entry.getValue().equals(plugin)) return entry.getKey();
        return null;
    }

    /**
     * 获取所有附属的名称
     * @deprecated 附属可能重名
     * */
    @Deprecated
    @NotNull
    public List<String> getExpansionNames() {
        return EXPANSION_MAP.values().stream().map(IExpansion::getExpansionName).collect(Collectors.toList());
    }

    /**
     * 获取所有附属的完整标识符
     * */
    @NotNull
    public Set<String> getExpansionKeySet() {
        return EXPANSION_MAP.keySet();
    }

    public File getPluginFolder() {
        return pluginFolder;
    }

}
