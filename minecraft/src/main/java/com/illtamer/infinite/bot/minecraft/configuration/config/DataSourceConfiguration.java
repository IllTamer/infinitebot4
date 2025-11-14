package com.illtamer.infinite.bot.minecraft.configuration.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

public class DataSourceConfiguration {

    private final HikariDataSource dataSource;

    @SneakyThrows(SQLException.class)
    public DataSourceConfiguration() {
        final Map<String, Object> mysqlConfig = BotConfiguration.database.mysqlConfig;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + mysqlConfig.get("host") + ":" + mysqlConfig.get("port") + "/" + mysqlConfig.get("database"));
        config.setUsername((String) mysqlConfig.get("username"));
        config.setPassword((String) mysqlConfig.get("password"));
        config.setMaximumPoolSize(20); // 根据需要调整，建议 10~30
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30_000);
        config.setIdleTimeout(600_000);
        config.setMaxLifetime(1800_000);
        config.setLeakDetectionThreshold(60_000); // 60秒未归还视为泄漏（开发环境可用）
        config.setAutoCommit(true);
        this.dataSource = new HikariDataSource(config);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

}
