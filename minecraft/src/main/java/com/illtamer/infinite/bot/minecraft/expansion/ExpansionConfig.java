package com.illtamer.infinite.bot.minecraft.expansion;

import com.illtamer.infinite.bot.minecraft.api.IExpansion;
import com.illtamer.infinite.bot.minecraft.configuration.config.CommentConfiguration;
import com.illtamer.perpetua.sdk.util.Assert;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class ExpansionConfig {
    private final String fileName;
    private final IExpansion expansion;
    private final Integer version;
    private File file;
    private volatile FileConfiguration config;

    public ExpansionConfig(String fileName, IExpansion expansion) {
        this(fileName, expansion, 0);
    }

    public ExpansionConfig(String fileName, IExpansion expansion, int version) {
        this.expansion = expansion;
        this.fileName = fileName;
        Assert.isTrue(version >= 0, "Illegal version %d", version);
        this.version = version;
        this.config = load();
        checkVersion();
    }

    /**
     * 将内存中的数据保存到硬盘
     * */
    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        config = load();
    }

    /**
     * 重新将硬盘的数据加载到内存中
     * */
    public void reload() {
        config = load();
    }

    /**
     * 获取文件对象
     * */
    public FileConfiguration getConfig() {
        return this.config;
    }

    private FileConfiguration load() {
        File file = new File(expansion.getDataFolder(), fileName);
        if (!file.exists()) {
            expansion.saveResource(fileName, false);
        }
        CommentConfiguration yaml = new CommentConfiguration();
        try {
            yaml.load(file);
            this.file = file;
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return yaml;
    }

    private void checkVersion() {
        final int depVersion = config.getInt("version", 0);
        switch (this.version.compareTo(depVersion)) {
            case 0: break;
            case -1: {
                expansion.getLogger().error("Deprecated expansion config version! Please try latest release.");
                break;
            }
            case 1: {
                backAndGenerateConfig(depVersion);
                break;
            }
        }
    }

    private void backAndGenerateConfig(int depVersion) {
        File backFile = new File(expansion.getDataFolder(), String.format("%s-%d-%d.bak", fileName, depVersion, System.currentTimeMillis()));
        Assert.isTrue(file.renameTo(backFile), "Can't rename file '%s'", file.getName());
        file = new File(expansion.getDataFolder(), fileName);
        try (
                InputStream input = expansion.getResource(fileName);
                FileOutputStream output = new FileOutputStream(file)
        ) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }
        } catch (IOException e) {
            expansion.getLogger().error("Read config failed.", e);
            return;
        }
        final Set<String> oldConfigKeys = config.getKeys(true);
        CommentConfiguration newConfig = new CommentConfiguration();
        try {
            newConfig.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            expansion.getLogger().error("Config " + fileName + " load failed", e);
        }
        // add/delete -> continue; update -> set
        for (String key : newConfig.getKeys(true)) {
            if (key.length() == 7 && "version".equals(key)) continue;
            if (!oldConfigKeys.contains(key) || newConfig.get(key) instanceof MemorySection) continue;
            newConfig.set(key, config.get(key));
        }
        config = newConfig;
        try {
            if (config.getInt("version") != version) {
                // 更新了代码版本，忘记更新配置版本号
                config.set("version", version);
                expansion.getLogger().warn("An deprecated version of embedded config was detected, the generate file was manually updated");
            }
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        expansion.getLogger().info("Config(version: " + depVersion + ") auto-update to version-" + version + " now ! (The previous has been backed up)");
    }

}
