package com.illtamer.infinite.bot.minecraft.configuration.config;

import com.illtamer.infinite.bot.api.util.Assert;
import com.illtamer.infinite.bot.minecraft.api.StaticAPI;
import com.illtamer.infinite.bot.minecraft.api.adapter.Bootstrap;
import com.illtamer.infinite.bot.minecraft.api.adapter.ConfigSection;
import com.illtamer.infinite.bot.minecraft.api.event.LocalEvent;
import com.illtamer.infinite.bot.minecraft.configuration.redis.ConfigurationCenter;
import com.illtamer.infinite.bot.minecraft.repository.PlayerDataRepository;
import com.illtamer.infinite.bot.minecraft.repository.impl.DatabasePlayerDataRepository;
import com.illtamer.infinite.bot.minecraft.repository.impl.YamlPlayerDataRepository;
import com.illtamer.infinite.bot.minecraft.start.bungee.BungeeBootstrap;
import com.illtamer.infinite.bot.minecraft.util.JedisUtil;
import lombok.Getter;
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
    public static RedisConfig redis;

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
        redis = new RedisConfig();
        if (StaticAPI.getInstance() instanceof BungeeBootstrap) {
            StaticAPI.getInstance().getLogger().info("检测到主配置重载，同步配置中");
            JedisUtil.publish(LocalEvent.builder()
                    .identify(ConfigurationCenter.IDENTIFY)
                    .data(configFile.getConfig().saveToString())
                    .build());
        }
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
        StaticAPI.setInstance(plugin);
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

    public class MainConfig {

        private ConfigSection section = configFile.getConfig().getSection("main");

        @NotNull
        public final Boolean bungee =section.getBoolean("bungee", false);

        @NotNull
        // TODO 发送合并节点消息？
        public final Boolean tryAvoidRiskControl = section.getBoolean("try-avoid-risk-control", false);

        @NotNull
        public final List<Long> admins = section.getLongList("admins");

        @NotNull
        public final List<Long> groups = section.getLongList("groups");

    }

    @SuppressWarnings("all")
    public class DatabaseConfig {

        private ConfigSection section = configFile.getConfig().getSection("database");

        @NotNull
        public final String type = section.getString("type", "yml");

        @NotNull
        public final Map<String, Object> mysqlConfig = Optional.ofNullable(configFile.getConfig().getSection("database.config.mysql")).orElseThrow(() -> new NullPointerException("Nonexistent path: database.config.mysql")).getValues(false);

    }

    @SuppressWarnings("all")
    public class ConnectionConfig {

        private ConfigSection section = configFile.getConfig().getSection("connection");

        @NotNull
        public final String host = section.getString("host", "unknown");

        public final int httpPort = section.getInt("port.http", -1);

        public final int websocketPort = section.getInt("port.websocket", -1);

        @Nullable
        public final String authorization = section.getString("authorization", null);

    }

    @SuppressWarnings("all")
    public class RedisConfig {

        private ConfigSection section = configFile.getConfig().getSection("redis");

        @NotNull
        public final Boolean embed = section.getBoolean("embed", true);

        @NotNull
        public final String host = section.getString("host", "127.0.0.1");

        @NotNull
        public final Integer port = section.getInt("port", 2380);

    }

}
