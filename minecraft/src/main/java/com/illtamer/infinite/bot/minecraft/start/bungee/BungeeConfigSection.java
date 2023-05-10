package com.illtamer.infinite.bot.minecraft.start.bungee;

import com.illtamer.infinite.bot.minecraft.api.adapter.ConfigSection;
import com.illtamer.infinite.bot.minecraft.api.adapter.Configuration;
import com.illtamer.infinite.bot.minecraft.util.Lambda;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

// TODO bug fix need: bc端无法从根节点直接递归读取数据，需指定顶级节点后方可使用 特性 / bug ?
class BungeeConfigSection implements ConfigSection {

    protected final net.md_5.bungee.config.Configuration config;

    private BungeeConfigSection(net.md_5.bungee.config.Configuration config) {
        this.config = config;
    }

    @Override
    public String getString(String path) {
        return config.getString(path);
    }

    @Override
    public String getString(String path, String def) {
        return config.getString(path, def);
    }

    @Override
    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    @Override
    public Integer getInt(String path) {
        return config.getInt(path);
    }

    @Override
    public Integer getInt(String path, Integer def) {
        return config.getInt(path, def);
    }

    @Override
    public Long getLong(String path) {
        return config.getLong(path);
    }

    @Override
    public Long getLong(String path, Long def) {
        return config.getLong(path, def);
    }

    @Override
    public Boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    @Override
    public Boolean getBoolean(String path, Boolean def) {
        return config.getBoolean(path);
    }

    @Override
    public List<Long> getLongList(String path) {
        return config.getLongList(path);
    }

    @Override
    public void set(String path, Object value) {
        config.set(path, value);
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return (Set<String>) config.getKeys();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> getValues(boolean deep) {
        try {
            Field selfField = net.md_5.bungee.config.Configuration.class.getDeclaredField("self");
            selfField.setAccessible(true);
            return (Map<String, Object>) selfField.get(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static class Config extends BungeeConfigSection implements Configuration {

        private static final ConfigurationProvider PROVIDER = ConfigurationProvider.getProvider(YamlConfiguration.class);
        private net.md_5.bungee.config.Configuration config;

        Config() {
            super(PROVIDER.load(""));
            this.config = super.config;
        }

        @Override
        public @Nullable ConfigSection getSection(String path) {
            return Lambda.nullableInvoke(BungeeConfigSection::new, config.getSection(path));
        }

        @Override
        public ConfigSection createSection(String path, Map<String, Object> data) {
            config.set(path, data);
            return Lambda.nullableInvoke(BungeeConfigSection::new, config.getSection(path));
        }

        @Override
        public String saveToString() {
            StringWriter writer = new StringWriter();
            PROVIDER.save(config, writer);
            return writer.toString();
        }

        @Override
        public void save(File file) throws IOException {
            PROVIDER.save(config, file);
        }

        @Override
        public void load(File file) throws IOException {
            this.config = PROVIDER.load(file);
        }

        @Override
        public void load(String yaml) {
            this.config = PROVIDER.load(yaml);
        }

    }

}
