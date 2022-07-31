package com.illtamer.infinite.bot.minecraft.expansion;

import com.illtamer.infinite.bot.api.util.Assert;
import com.illtamer.infinite.bot.minecraft.Bootstrap;
import com.illtamer.infinite.bot.minecraft.api.IExpansion;
import com.illtamer.infinite.bot.minecraft.exception.InvalidExpansionException;
import com.illtamer.infinite.bot.minecraft.expansion.manager.InfinitePluginLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * 附属总加载类
 * */
public class ExpansionLoader {
    private static final InfinitePluginLoader PLUGIN_LOADER = new InfinitePluginLoader();
    private static final List<IExpansion> EXPANSIONS = new CopyOnWriteArrayList<>();
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
     * 加载附属插件并调用其onEnable
     * */
    public void loadExpansion(File file) {
        if (file.isDirectory() || !file.getName().endsWith(".jar")) return;
        IExpansion expansion = null;
        try {
            EXPANSIONS.add(expansion = PLUGIN_LOADER.loadExpansion(file));
            PLUGIN_LOADER.enableExpansion(expansion);
        } catch (InvalidExpansionException e) {
            e.printStackTrace();
        }
        Assert.isTrue(expansion != null && expansion.isEnabled(), String.format("附属 %s 异常加载!", file.getName()));
    }

    /**
     * 加载所有插件并调用其onEnable
     * */
    public void loadExpansions() {
        if (EXPANSIONS.size() != 0) {
            disableExpansions();
        }
        File[] expansions = pluginFolder.listFiles((dir, name) -> name.endsWith(".jar"));
        if (expansions == null || expansions.length == 0) {
            return;
        }
        for (File expansion : expansions) {
            if (expansion.isDirectory()) continue;
            try {
                EXPANSIONS.add(PLUGIN_LOADER.loadExpansion(expansion));
            } catch (InvalidExpansionException e) {
                e.printStackTrace();
            }
        }
        log.info(String.format("检测到 %s 个附属, 正在初始化...", EXPANSIONS.size()));
        for (IExpansion expansion : EXPANSIONS) {
            Assert.isTrue(!expansion.isEnabled(), String.format("附属 %s 异常加载!", expansion.getExpansionName()));
            PLUGIN_LOADER.enableExpansion(expansion);
        }
    }

    /**
     * 卸载所有插件并调用其所有onDisable
     * */
    public void disableExpansions() {
        for (IExpansion expansion : EXPANSIONS) {
            PLUGIN_LOADER.disableExpansion(expansion);
            EXPANSIONS.remove(expansion);
        }
        System.gc();
    }

    /**
     * 卸载指定附属
     * */
    public boolean disableExpansion(String expansionName) {
        for (IExpansion expansion : EXPANSIONS) {
            if (expansion.getExpansionName().equals(expansionName)) {
                PLUGIN_LOADER.disableExpansion(expansion);
                return EXPANSIONS.remove(expansion);
            }
        }
        return false;
    }

    @Nullable
    public IExpansion getExpansion(String name) {
        for (IExpansion expansion : EXPANSIONS) {
            if (expansion.getExpansionName().equals(name))
                return expansion;
        }
        return null;
    }

    /**
     * 获取所有附属的名称
     * */
    @NotNull
    public List<String> getExpansionNames() {
        List<String> list = new ArrayList<>();
        for (IExpansion expansion : EXPANSIONS) {
            list.add(expansion.getExpansionName());
        }
        return list;
    }

    public File getPluginFolder() {
        return pluginFolder;
    }

}
