package com.illtamer.infinite.bot.minecraft.configuration.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ConfigFile {
    private final String name;
    private final Plugin instance;
    private File file;
    private volatile FileConfiguration config;

    public ConfigFile(String name, Plugin instance) {
        this.name = name;
        this.instance = instance;
        this.config = this.load();
    }

    public void save() {
        try {
            this.config.save(this.file);
        } catch (IOException var2) {
            var2.printStackTrace();
        }
        this.config = this.load();
    }

    private FileConfiguration load() {
        File file = new File(this.instance.getDataFolder(), this.name);
        if (!file.exists()) {
            this.instance.saveResource(this.name, false);
        }

        YamlConfiguration yaml = new YamlConfiguration();

        try {
            yaml.load(file);
            this.file = file;
        } catch (IOException | InvalidConfigurationException var4) {
            var4.printStackTrace();
        }

        return yaml;
    }

    public void reload() {
        this.config = this.load();
    }

    public FileConfiguration getConfig() {
        return this.config;
    }
}
