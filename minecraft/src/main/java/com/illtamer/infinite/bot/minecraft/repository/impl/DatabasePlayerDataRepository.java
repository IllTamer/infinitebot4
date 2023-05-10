package com.illtamer.infinite.bot.minecraft.repository.impl;

import com.illtamer.infinite.bot.api.util.Assert;
import com.illtamer.infinite.bot.minecraft.api.adapter.Bootstrap;
import com.illtamer.infinite.bot.minecraft.start.bukkit.BukkitBootstrap;
import com.illtamer.infinite.bot.minecraft.pojo.PlayerData;
import com.illtamer.infinite.bot.minecraft.repository.PlayerDataRepository;
import com.illtamer.infinite.bot.minecraft.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class DatabasePlayerDataRepository implements PlayerDataRepository {

    private final Logger log;
    protected final DataSource dataSource;

    public DatabasePlayerDataRepository(@NotNull DataSource dataSource, Bootstrap instance) {
        this.log = instance.getLogger();
        this.dataSource = dataSource;
        createTableIfNotExists();
    }

    @Override
    public boolean save(@NotNull PlayerData data) {
        boolean execute = false;
        try (final Connection connection = getConnection()) {
            PlayerData saved = getPlayerDataByUUIDorUserId(data);
            if (saved != null) {
                log.warning(String.format("PlayerData(%s) is already exists!", saved));
                return false;
            }
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO `infinite_bot_3` (uuid, valid_uuid, user_id, permission) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, data.getUuid());
            ps.setString(2, data.getValidUUID());
            ps.setLong(3, data.getUserId());
            ps.setString(4, StringUtil.parseString(data.getPermission()));
            execute = ps.execute();
            setId(data, ps);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return execute;
    }

    @Override
    public @Nullable PlayerData queryByUUID(@NotNull UUID uuid) {
        return queryByUUID(uuid.toString());
    }

    @Override
    public @Nullable PlayerData queryByUUID(@NotNull String uuid) {
        PlayerData data = null;
        try {
            data = doQueryByUUID(uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public @Nullable PlayerData queryByUserId(@NotNull Long userId) {
        PlayerData data = null;
        try {
            data = doQueryByUserId(userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    @Nullable
    public PlayerData update(@NotNull PlayerData data) {
        PlayerData saved = null;
        try (final Connection connection = getConnection()) {
            saved = getPlayerDataByUUIDorUserId(data);
            if (saved == null) {
                log.warning(String.format("PlayerData(%s) can not be found!", data));
                return null;
            }
            PreparedStatement ps = connection.prepareStatement("UPDATE `infinite_bot_3` SET uuid=?, valid_uuid=?, user_id=?, permission=? WHERE id=?");
            ps.setString(1, data.getUuid());
            ps.setString(2, data.getValidUUID());
            ps.setLong(3, data.getUserId());
            ps.setString(4, StringUtil.parseString(data.getPermission()));
            ps.setInt(5, saved.getId());
            if (!(ps.executeUpdate() > 0))
                log.warning("An exception occurred while updating " + data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return saved;
    }

    @Override
    @Nullable
    public PlayerData delete(@NotNull String uuid) {
        PlayerData saved = null;
        try {
            saved = doQueryByUUID(uuid);
            if (saved == null) return null;
            if (!doDeleteById(saved.getId()))
                log.warning("An exception occurred while deleting by uuid: " + uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return saved;
    }

    @Override
    @Nullable
    public PlayerData delete(@NotNull Long userId) {
        PlayerData saved = null;
        try {
            saved = doQueryByUserId(userId);
            if (saved == null) return null;
            if (!doDeleteById(saved.getId()))
                log.warning("An exception occurred while deleting by userId: " + userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return saved;
    }

    @Override
    public void saveCacheData() {
        // caching may be required in the future
    }

    @Nullable
    protected PlayerData getPlayerDataByUUIDorUserId(PlayerData data) throws SQLException {
        PlayerData query = null;
        final String uuid = data.getUuid() == null ? data.getValidUUID() : data.getUuid();
        if (uuid != null) {
            query = doQueryByUUID(uuid);
        }
        if (query == null) {
            final Long userId = data.getUserId();
            Assert.notNull(userId, "At least one of 'uuid' and 'userId' can not be null");
            query = doQueryByUserId(userId);
        }
        return query;
    }

    @SuppressWarnings("all")
    protected void createTableIfNotExists() {
        final Statement statement;
        try (final Connection connection = getConnection()) {
            statement = connection.createStatement();
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS `infinite_bot_3` (\n" +
                            "  `id` int NOT NULL AUTO_INCREMENT,\n" +
                            "  `uuid` varchar(45) DEFAULT NULL,\n" +
                            "  `valid_uuid` varchar(45) DEFAULT NULL,\n" +
                            "  `user_id` BIGINT(16) DEFAULT NULL,\n" +
                            "  `permission` text,\n" +
                            "  PRIMARY KEY (`id`)\n" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8"
            );
        } catch (SQLException e) {
            log.warning("Some exceptions occurred when create table 'infinite_bot_3'");
            e.printStackTrace();
        }
    }

    protected boolean doDeleteById(int id) throws SQLException {
        try (final Connection connection = getConnection()) {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM `infinite_bot_3` WHERE id=?");
            ps.setInt(1, id);
            return ps.execute();
        }
    }

    @Nullable
    protected PlayerData doQueryByUUID(@NotNull String uuid) throws SQLException {
        try (final Connection connection = getConnection()) {
            PreparedStatement ps;
            String sql = "SELECT * FROM `infinite_bot_3` WHERE uuid = ? OR valid_uuid = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, uuid);
            ps.setString(2, uuid);
            final List<PlayerData> list = parseResultSet(ps.executeQuery());
            return singletonEntity(list);
        }
    }

    @Nullable
    protected PlayerData doQueryByUserId(@NotNull Long userId) throws SQLException {
        try (final Connection connection = getConnection()) {
            PreparedStatement ps;
            String sql = "SELECT * FROM `infinite_bot_3` WHERE user_id = ?";
            ps = connection.prepareStatement(sql);
            ps.setLong(1, userId);
            final List<PlayerData> list = parseResultSet(ps.executeQuery());
            return singletonEntity(list);
        }
    }

//    @SneakyThrows(SQLException.class)
    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private static <T> T singletonEntity(@NotNull List<T> list) {
        Assert.isTrue(list.size() <= 1, "Duplicate player data: %s", list);
        if (list.size() == 0) return null;
        else return list.get(0);
    }

    @NotNull
    private static List<PlayerData> parseResultSet(ResultSet set) throws SQLException {
        List<PlayerData> list = new ArrayList<>();
        while (set.next()) {
            PlayerData data = new PlayerData();
            final int id = set.getInt("id");
            data.setId(id == 0 ? null : id);
            data.setUuid(set.getString("uuid"));
            data.setValidUUID(set.getString("valid_uuid"));
            final long userId = set.getLong("user_id");
            data.setUserId(userId != 0 ? userId : null);
            data.setPermission(StringUtil.parseList(set.getString("permission")));
            list.add(data);
        }
        return list;
    }

    private static void setId(PlayerData data, Statement statement) throws SQLException{
        final ResultSet keys = statement.getGeneratedKeys();
        if (keys.next()) {
            data.setId(keys.getInt(1));
        }
    }

}
