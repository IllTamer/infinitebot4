package com.illtamer.infinite.bot.minecraft.expansion;

import com.illtamer.infinite.bot.minecraft.api.IExpansion;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ExpansionConfig {
    private final String fileName;
    private final IExpansion expansion;
    private File file;
    private volatile FileConfiguration config;

    public ExpansionConfig(String fileName, IExpansion expansion) {
        this.expansion = expansion;
        this.fileName = fileName;
        this.config = load();
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
}
