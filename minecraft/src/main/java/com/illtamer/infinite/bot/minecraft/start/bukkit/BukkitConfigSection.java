package com.illtamer.infinite.bot.minecraft.start.bukkit;

import com.illtamer.infinite.bot.minecraft.api.adapter.ConfigSection;
import com.illtamer.infinite.bot.minecraft.api.adapter.Configuration;
import com.illtamer.infinite.bot.minecraft.util.Lambda;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

class BukkitConfigSection implements ConfigSection {

    protected final ConfigurationSection section;

    private BukkitConfigSection(ConfigurationSection section) {
        this.section = section;
    }

    @Override
    public String getString(String path) {
        return section.getString(path);
    }

    @Override
    public String getString(String path, String def) {
        return section.getString(path, def);
    }

    @Override
    public List<String> getStringList(String path) {
        return section.getStringList(path);
    }

    @Override
    public Integer getInt(String path) {
        return section.getInt(path);
    }

    @Override
    public Integer getInt(String path, Integer def) {
        return section.getInt(path, def);
    }

    @Override
    public Long getLong(String path) {
        return section.getLong(path);
    }

    @Override
    public Long getLong(String path, Long def) {
        return section.getLong(path, def);
    }

    @Override
    public Boolean getBoolean(String path) {
        return section.getBoolean(path);
    }

    @Override
    public Boolean getBoolean(String path, Boolean def) {
        return section.getBoolean(path, def);
    }

    @Override
    public List<Long> getLongList(String path) {
        return section.getLongList(path);
    }

    @Override
    public void set(String path, Object value) {
        section.set(path, value);
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return section.getKeys(deep);
    }

    @Override
    public Map<String, Object> getValues(boolean deep) {
        return section.getValues(deep);
    }

    static class Config extends BukkitConfigSection implements Configuration {

        private final YamlConfiguration yaml;

        Config() {
            super(new YamlConfiguration());
            this.yaml = (YamlConfiguration) super.section;
        }

        @Nullable
        @Override
        public ConfigSection getSection(String path) {
            return Lambda.nullableInvoke(BukkitConfigSection::new, yaml.getConfigurationSection(path));
        }

        @Override
        public ConfigSection createSection(String path, Map<String, Object> data) {
            return new BukkitConfigSection(yaml.createSection(path, data));
        }

        @Override
        public String saveToString() {
            return yaml.saveToString();
        }

        @Override
        public void save(File file) throws IOException {
            yaml.save(file);
        }

        @Override
        public void load(File file) throws IOException {
            try {
                yaml.load(file);
            } catch (InvalidConfigurationException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void load(String yaml) {
            try {
                this.yaml.load(yaml);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

}
