package com.illtamer.infinite.bot.minecraft.configuration.config;

import com.illtamer.infinite.bot.minecraft.api.adapter.Bootstrap;
import com.illtamer.infinite.bot.minecraft.api.adapter.ConfigSection;
import com.illtamer.infinite.bot.minecraft.repository.PlayerDataRepository;
import com.illtamer.infinite.bot.minecraft.repository.impl.DatabasePlayerDataRepository;
import com.illtamer.infinite.bot.minecraft.repository.impl.YamlPlayerDataRepository;
import com.illtamer.perpetua.sdk.util.Assert;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BotConfiguration {

    public static final String EXPANSION_FOLDER_NAME = "expansions";

    @Getter
    private static volatile BotConfiguration instance;

    public static MainConfig main;
    public static DatabaseConfig database;
    public static ConnectionConfig connection;

    @Getter
    private final ConfigFile configFile;
    @Getter
    private final PlayerDataRepository repository;

    private BotConfiguration(Bootstrap instance) {
        this.configFile = new ConfigFile("config.yml", instance);
        loadConfigurations();
        new File(instance.getDataFolder(), EXPANSION_FOLDER_NAME).mkdirs();
        boolean localData = "yaml".equalsIgnoreCase(database.type);
        DataSource dataSource = null;
        if (!localData) {
            dataSource = new DataSourceConfiguration().getDataSource();
            try (Connection connection = dataSource.getConnection()) {
                ; // do nothing
            } catch (SQLException ignore) {
                localData = true;
                instance.getLogger().warning("数据库连接异常，自动启用本地数据，如需使用数据库请修改后重新载入插件");
            }
        }
        repository = localData ?
                new YamlPlayerDataRepository(new ConfigFile("player_data.yml", instance), instance) :
                new DatabasePlayerDataRepository(dataSource, instance);
    }

    /**
     * 从字符串加载配置文件
     * */
    public void loadConfigurations(String yaml) {
        this.configFile.update(yaml);
        loadConfigurations();
    }

    /**
     * 加载配置文件
     * */
    private void loadConfigurations() {
        main = new MainConfig();
        database = new DatabaseConfig();
        connection = new ConnectionConfig();
    }

    /**
     * 加载配置类
     * @return Singleton instance
     * */
    public static BotConfiguration load(Bootstrap plugin) {
        Assert.isNull(instance, "Repeated initialization");
        synchronized (BotConfiguration.class) {
            Assert.isNull(instance, "Repeated initialization");
            instance = new BotConfiguration(plugin);
        }
        return instance;
    }

    /**
     * 重载配置类
     * @apiNote 仅重载 config.yml 文件内容，其它组件不提供重载支持
     * */
    public static void reload() {
        instance.configFile.reload();
        instance.loadConfigurations();
    }

    /**
     * 保存缓存中改动的数据
     * */
    public static void saveAndClose() {
        instance.repository.saveCacheData();
    }

    @ToString
    @SuppressWarnings("all")
    public class MainConfig {

        private ConfigSection section = configFile.getConfig().getSection("main");

        @NotNull
        public final List<Long> admins = section.getLongList("admins");

        @NotNull
        public final List<Long> groups = section.getLongList("groups");

    }

    @ToString
    @SuppressWarnings("all")
    public class DatabaseConfig {

        private ConfigSection section = configFile.getConfig().getSection("database");

        @NotNull
        public final String type = section.getString("type", "yml");

        @NotNull
        public final Map<String, Object> mysqlConfig = Optional.ofNullable(configFile.getConfig().getSection("database.config.mysql")).orElseThrow(() -> new NullPointerException("Nonexistent path: database.config.mysql")).getValues(false);

    }

    @ToString
    @SuppressWarnings("all")
    public class ConnectionConfig {

        private ConfigSection section = configFile.getConfig().getSection("connection");

        @NotNull
        public final String name = section.getString("name", "");

        @NotNull
        public final boolean master = section.getBoolean("master", false);

        @NotNull
        public final String host = section.getString("webapi.host", "unknown");

        public final int port = section.getInt("webapi.port", -1);

        @Nullable
        public final String authorization = section.getString("authorization", null);

    }

}
