package com.illtamer.infinite.bot.minecraft.expansion.manager;

import com.illtamer.infinite.bot.api.util.Assert;
import com.illtamer.infinite.bot.minecraft.Bootstrap;
import com.illtamer.infinite.bot.minecraft.api.IExpansion;
import com.illtamer.infinite.bot.minecraft.configuration.config.BotConfiguration;
import com.illtamer.infinite.bot.minecraft.expansion.ExpansionLogger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public abstract class InfiniteExpansion implements IExpansion {
    private boolean isEnabled = false;
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
        return isEnabled;
    }

    final void init(InfinitePluginLoader loader, File jarFile, ClassLoader classLoader, String folderName) {
        this.loader = loader;
        this.jarFile = jarFile;
        this.classLoader = classLoader;
        this.logger = new ExpansionLogger(this);
        this.dataFolder = new File(Bootstrap.getInstance().getDataFolder(), '/' + BotConfiguration.EXPANSION_FOLDER_NAME + '/' + folderName);
    }

    /**
     * bot内部开启/关闭拓展
     * */
    protected void setEnabled(boolean enabled) {
        if (this.isEnabled != enabled) {
            this.isEnabled = enabled;
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
        URL url = classLoader.getResource(name);
        if (url == null) {
            return null;
        }
        try {
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void saveResource(String path, boolean replace) {
        Assert.isTrue(path != null && !path.isEmpty(), "The resource name can not be null !");
        path = path.replace("\\", "/");
        InputStream input = getResource(path);
        Assert.notNull(input, String.format("Can't find the resource '%s' in %s", path, jarFile));

        File outFile = new File(this.dataFolder, path);
        int lastIndex = path.lastIndexOf('/');
        File outDir = new File(this.dataFolder, path.substring(0, Math.max(lastIndex, 0)));
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = input.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                input.close();
            } else {
                this.logger.warn("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists !");
            }
        } catch (IOException ex) {
            this.logger.error("Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    @Override
    public File getDataFolder() {
        return dataFolder;
    }

    @Override
    public ExpansionLogger getLogger() {
        return logger;
    }

}
