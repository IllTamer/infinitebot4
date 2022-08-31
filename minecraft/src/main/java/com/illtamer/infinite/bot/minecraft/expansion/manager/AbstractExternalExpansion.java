package com.illtamer.infinite.bot.minecraft.expansion.manager;

import com.illtamer.infinite.bot.api.util.Assert;
import com.illtamer.infinite.bot.minecraft.Bootstrap;
import com.illtamer.infinite.bot.minecraft.api.IExternalExpansion;
import com.illtamer.infinite.bot.minecraft.configuration.config.BotConfiguration;
import com.illtamer.infinite.bot.minecraft.expansion.ExpansionLogger;
import com.illtamer.infinite.bot.minecraft.util.ExpansionUtil;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;

public abstract class AbstractExternalExpansion implements IExternalExpansion {

    private final ClassLoader classLoader;
    private final File dataFolder;
    private final ExpansionLogger logger;

    private boolean register;
    private boolean enabled;

    public AbstractExternalExpansion() {
        this.classLoader = this.getClass().getClassLoader();
        Assert.notEmpty(getExpansionName(), "Expansion name can not be empty!");
        this.dataFolder = new File(Bootstrap.getInstance().getDataFolder(), '/' + BotConfiguration.EXPANSION_FOLDER_NAME + '/' + getExpansionName());
        this.logger = new ExpansionLogger(this);
    }

    @Override
    public void register(@NotNull Plugin plugin) {
        Bootstrap.getInstance().getExpansionLoader().loadExternalExpansion(this, plugin);
        register = true;
    }

    @Override
    public void unregister() {
        Bootstrap.getInstance().getExpansionLoader().disableExternalExpansion(this);
        register = false;
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

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public File getDataFolder() {
        return dataFolder;
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
        Assert.notNull(input, String.format("Can't find the resource '%s' in %s", path, classLoader));
        ExpansionUtil.savePluginResource(path, replace, dataFolder, input);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isRegister() {
        return register;
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
