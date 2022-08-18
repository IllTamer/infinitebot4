package com.illtamer.infinite.bot.minecraft.expansion.manager;


import com.illtamer.infinite.bot.api.util.Assert;
import com.illtamer.infinite.bot.minecraft.Bootstrap;
import com.illtamer.infinite.bot.minecraft.api.EventExecutor;
import com.illtamer.infinite.bot.minecraft.api.IExpansion;
import com.illtamer.infinite.bot.minecraft.exception.InvalidExpansionException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * 附属管理类
 * */
public class InfinitePluginLoader {
    private final Map<String, Class<?>> globalClasses = new ConcurrentHashMap<>();
    private final List<PluginClassLoader> loaders = new CopyOnWriteArrayList<>();

    /**
     * @param jarFile 加载一个已经过命名检查的
     *      存在的jarFile
     * */
    public IExpansion loadExpansion(File jarFile) throws InvalidExpansionException {
        PluginClassLoader loader;
        try {
            loader = new PluginClassLoader(this, getClass().getClassLoader(), jarFile);
            loaders.add(loader);
        } catch (Exception e) {
            throw new InvalidExpansionException(e);
        }
        return loader.expansion;
    }

    /**
     * 开启插件
     * */
    public void enableExpansion(IExpansion expansion) {
        Assert.isTrue(expansion instanceof InfiniteExpansion, "Expansion is not associated with this PluginLoader");
        if (!expansion.isEnabled()) {
            final Logger logger = Bootstrap.getInstance().getLogger();
            logger.info(String.format("Enabling %s", expansion.getExpansionName()));
            InfiniteExpansion infiniteExpansion = (InfiniteExpansion) expansion;
            PluginClassLoader pluginLoader = (PluginClassLoader)infiniteExpansion.getClassLoader();

            if (!this.loaders.contains(pluginLoader)) {
                this.loaders.add(pluginLoader);
                logger.warning("Enabled plugin with unregistered PluginClassLoader " + infiniteExpansion.getExpansionName());
            }

            try {
                infiniteExpansion.setEnabled(true);
            } catch (Throwable ex) {
                logger.severe("Error occurred while enabling " + infiniteExpansion.getExpansionName() + " (Is it up to date?)");
                ex.printStackTrace();
            }
            // 启用监听
        }
    }

    /**
     * 关闭插件
     * */
    public void disableExpansion(IExpansion expansion) {
        Assert.isTrue(expansion instanceof InfiniteExpansion, "Expansion is not associated with this PluginLoader");

        if (expansion.isEnabled()) {
            final Logger logger = Bootstrap.getInstance().getLogger();
            logger.info(String.format("Disabling %s", expansion.getExpansionName()));
            // 注销监听
            EventExecutor.unregisterListeners(expansion);
            EventExecutor.unregisterBukkitEventForExpansion(expansion);

            InfiniteExpansion infiniteExpansion = (InfiniteExpansion) expansion;
            ClassLoader classLoader = infiniteExpansion.getClassLoader();

            try {
                infiniteExpansion.setEnabled(false);
            } catch (Throwable ex) {
                logger.severe("Error occurred while disabling " + infiniteExpansion.getExpansionName() + " (Is it up to date?)");
                ex.printStackTrace();
            }

            if (classLoader instanceof PluginClassLoader) {
                PluginClassLoader loader = (PluginClassLoader) classLoader;
                this.loaders.remove(loader);
                Set<String> names = loader.getClasses();
                for (String name : names) {
                    removeGlobalClasses(name);
                }
                try {
                    loader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void setGlobalClasses(String name, Class<?> clazz) {
        if (!globalClasses.containsKey(name)) {
            globalClasses.put(name, clazz);
        }
    }

    Class<?> getGlobalClassByName(String name) {
        Class<?> cachedClass = globalClasses.get(name);
        if (cachedClass != null) {
            return cachedClass;
        }
        for (PluginClassLoader loader : this.loaders) {
            try {
                cachedClass = loader.findClass(name, false);
            } catch (ClassNotFoundException ignore) {}
            if (cachedClass != null) {
                return cachedClass;
            }
        }
        return null;
    }

    /**
     * @see #disableExpansion(IExpansion)
     * */
    private void removeGlobalClasses(String name) {
        globalClasses.remove(name);
    }

    public Map<String, Class<?>> getGlobalClasses() {
        return globalClasses;
    }
}
