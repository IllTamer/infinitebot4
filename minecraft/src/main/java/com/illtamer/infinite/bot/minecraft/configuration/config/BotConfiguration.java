package com.illtamer.infinite.bot.minecraft.configuration.config;

import com.illtamer.infinite.bot.api.util.Assert;
import com.illtamer.infinite.bot.minecraft.api.adapter.Bootstrap;
import com.illtamer.infinite.bot.minecraft.start.bukkit.BukkitBootstrap;
import com.illtamer.infinite.bot.minecraft.repository.PlayerDataRepository;
import com.illtamer.infinite.bot.minecraft.repository.impl.DatabasePlayerDataRepository;
import com.illtamer.infinite.bot.minecraft.repository.impl.YamlPlayerDataRepository;
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
        new File(BukkitBootstrap.getInstance().getDataFolder(), EXPANSION_FOLDER_NAME).mkdirs();
        boolean localData = "yaml".equalsIgnoreCase(database.type);
        DataSource dataSource = null;
        if (!localData) {
            dataSource = new DataSourceConfiguration().getDataSource();
            try (Connection connection = dataSource.getConnection()) {
                ; // do nothing
            } catch (SQLException ignore) {
                localData = true;
                BukkitBootstrap.getInstance().getLogger().warning("数据库连接异常，自动启用本地数据，如需使用数据库请修改后重新载入插件");
            }
        }
        repository = localData ?
                new YamlPlayerDataRepository(new ConfigFile("player_data.yml", BukkitBootstrap.getInstance())) :
                new DatabasePlayerDataRepository(dataSource);
    }

    /**
     * 加载配置文件
     * */
    private void loadConfigurations() {
        main = new MainConfig();
        database = new DatabaseConfig();
        connection = new ConnectionConfig();
        redis = new RedisConfig();
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

    public class MainConfig {

        private static final String PATH = "main.";

        // TODO 发送合并节点消息？
        public final boolean tryAvoidRiskControl = configFile.getConfig().getBoolean(PATH + "try-avoid-risk-control", false);

        @NotNull
        public final List<Long> admins = configFile.getConfig().getLongList(PATH + "admins");

        @NotNull
        public final List<Long> groups = configFile.getConfig().getLongList(PATH + "groups");

    }

    @SuppressWarnings("all")
    public class DatabaseConfig {

        private static final String PATH = "database.";

        @NotNull
        public final String type = configFile.getConfig().getString(PATH + "type", "yml");

        @NotNull
        public final Map<String, Object> mysqlConfig = Optional.ofNullable(configFile.getConfig().getSection(PATH + "config.mysql")).orElseThrow(() -> new NullPointerException("Nonexistent path: " + PATH + "config.mysql")).getValues(false);

    }

    @SuppressWarnings("all")
    public class ConnectionConfig {

        private static final String PATH = "connection.";

        @NotNull
        public final String host = configFile.getConfig().getString(PATH + "host", "unknown");

        public final int httpPort = configFile.getConfig().getInt(PATH + "port.http", -1);

        public final int websocketPort = configFile.getConfig().getInt(PATH + "port.websocket", -1);

        @Nullable
        public final String authorization = configFile.getConfig().getString(PATH + "authorization", null);

    }

    @SuppressWarnings("all")
    public class RedisConfig {

        private static final String PATH = "redis.";

        @NotNull
        public final Boolean embed = configFile.getConfig().getBoolean(PATH + "embed", true);

        @NotNull
        public final String host = configFile.getConfig().getString(PATH + "host", "127.0.0.1");

        @NotNull
        public final Integer port = configFile.getConfig().getInt(PATH + "port", 2380);

    }

}
