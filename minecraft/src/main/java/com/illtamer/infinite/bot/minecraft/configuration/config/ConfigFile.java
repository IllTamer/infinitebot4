package com.illtamer.infinite.bot.minecraft.configuration.config;

import com.illtamer.infinite.bot.minecraft.api.adapter.Bootstrap;
import com.illtamer.infinite.bot.minecraft.api.adapter.Configuration;

import java.io.File;
import java.io.IOException;

public class ConfigFile {
    private final String name;
    private final Bootstrap instance;
    private File file;
    private volatile Configuration config;

    public ConfigFile(String name, Bootstrap instance) {
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

    /**
     * 更新并保存配置文件
     * */
    public void update(String yaml) {
        config.load(yaml);
        save();
    }

    private Configuration load() {
        File file = new File(this.instance.getDataFolder(), this.name);
        if (!file.exists()) {
            this.instance.saveResource(this.name, false);
        }

        Configuration configuration = instance.createConfig();
        try {
            configuration.load(file);
            this.file = file;
        } catch (IOException var4) {
            var4.printStackTrace();
        }
        return configuration;
    }

    public void reload() {
        this.config = this.load();
    }

    public Configuration getConfig() {
        return this.config;
    }

}
