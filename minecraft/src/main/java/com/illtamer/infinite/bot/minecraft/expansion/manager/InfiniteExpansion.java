package com.illtamer.infinite.bot.minecraft.expansion.manager;

import com.illtamer.infinite.bot.api.util.Assert;
import com.illtamer.infinite.bot.minecraft.Bootstrap;
import com.illtamer.infinite.bot.minecraft.api.IExpansion;
import com.illtamer.infinite.bot.minecraft.configuration.config.BotConfiguration;
import com.illtamer.infinite.bot.minecraft.expansion.ExpansionLogger;
import com.illtamer.infinite.bot.minecraft.util.ExpansionUtil;
import com.illtamer.infinite.bot.minecraft.util.PluginUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;

public abstract class InfiniteExpansion implements IExpansion {
    private boolean enabled = false;
    private InfinitePluginLoader loader;
    private File jarFile;
    private ClassLoader classLoader;
    private ExpansionLogger logger;
    private File dataFolder;

    public InfiniteExpansion() {
        ClassLoader classLoader = getClass().getClassLoader();
        if (!(classLoader instanceof PluginClassLoader)) {
            throw new IllegalStateException("InfiniteExpansion requires " + PluginClassLoader.class.getName());
        }
        ((PluginClassLoader)classLoader).initialize(this);
    }

    protected InfiniteExpansion(InfinitePluginLoader loader, File jarFile, String folderName) {
        this.loader = loader;
        ClassLoader classLoader = getClass().getClassLoader();
        if (classLoader instanceof PluginClassLoader) {
            throw new IllegalStateException("Cannot use initialization constructor at runtime");
        }
        init(loader, jarFile, classLoader, folderName);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    final void init(InfinitePluginLoader loader, File jarFile, ClassLoader classLoader, String folderName) {
        this.loader = loader;
        this.jarFile = jarFile;
        this.classLoader = classLoader;
        this.logger = new ExpansionLogger(this);
        folderName = getExpansionName() != null && getExpansionName().length() != 0 ? getExpansionName() : folderName;
        this.dataFolder = new File(Bootstrap.getInstance().getDataFolder(), '/' + BotConfiguration.EXPANSION_FOLDER_NAME + '/' + folderName);
    }

    /**
     * bot内部开启/关闭拓展
     * */
    protected void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }

    protected ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public InputStream getResource(String name) {
        return ExpansionUtil.getPluginResource(name, classLoader);
    }

    @Override
    public void saveResource(String path, boolean replace) {
        Assert.isTrue(path != null && !path.isEmpty(), "The resource name can not be null !");
        path = path.replace("\\", "/");
        InputStream input = getResource(path);
        Assert.notNull(input, String.format("Can't find the resource '%s' in %s", path, jarFile));
        ExpansionUtil.savePluginResource(path, replace, dataFolder, input);
    }

    @Override
    public File getDataFolder() {
        return dataFolder;
    }

    @Override
    public ExpansionLogger getLogger() {
        return logger;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getExpansionName(), getVersion(), getAuthor());
    }

    @Override
    @NotNull
    public String toString() {
        return ExpansionUtil.formatIdentifier(this);
    }

}
