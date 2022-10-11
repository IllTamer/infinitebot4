package com.illtamer.infinite.bot.minecraft.expansion;

import com.illtamer.infinite.bot.api.util.Assert;
import com.illtamer.infinite.bot.minecraft.api.IExpansion;
import com.illtamer.infinite.bot.minecraft.util.ExpansionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.ByteBuffer;
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
        YamlConfiguration yaml = new YamlConfiguration();
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
        YamlConfiguration newConfig = YamlConfiguration.loadConfiguration(file);

        // add/delete -> continue; update -> set
        for (String key : newConfig.getKeys(true)) {
            if (key.length() == 7 && "version".equals(key)) continue;
            if (!oldConfigKeys.contains(key)) continue;
            newConfig.set(key, config.get(key));
        }
        config = newConfig;
        try {
            ExpansionUtil.savePluginResource(fileName, true, expansion.getDataFolder(), new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        expansion.getLogger().info("Config auto-update success! (The previous has been backed up)");
    }

}
