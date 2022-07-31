package com.illtamer.infinite.bot.minecraft.configuration.config;

import com.mysql.cj.jdbc.MysqlDataSource;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

public class DataSourceConfiguration {

    private final MysqlDataSource dataSource;

    @SneakyThrows(SQLException.class)
    public DataSourceConfiguration() {
        final Map<String, Object> mysqlConfig = BotConfiguration.database.mysqlConfig;
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setServerName((String) mysqlConfig.get("host"));
        dataSource.setPort((Integer) mysqlConfig.get("port"));
        dataSource.setUser((String) mysqlConfig.get("username"));
        dataSource.setPassword((String) mysqlConfig.get("password"));
        dataSource.setDatabaseName((String) mysqlConfig.get("database"));
        dataSource.setAutoReconnect(true);
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

}
