package com.illtamer.infinite.bot.minecraft.configuration;

import com.illtamer.infinite.bot.api.util.Assert;
import com.illtamer.infinite.bot.minecraft.api.BotScheduler;
import com.illtamer.infinite.bot.minecraft.api.adapter.Bootstrap;
import com.illtamer.infinite.bot.minecraft.configuration.config.BotConfiguration;
import com.illtamer.infinite.bot.minecraft.util.JedisUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import redis.embedded.RedisExecProvider;
import redis.embedded.RedisServer;
import redis.embedded.util.Architecture;
import redis.embedded.util.OS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public abstract class EmbedRedisStarter extends Plugin {

    public static final String EXEC_NAME = "redis-server-2.8.19";

    @Getter
    protected static EmbedRedisStarter instance;

    @Nullable
    protected RedisServer server;

    @Override
    public void onLoad() {
        instance = this;
        BotConfiguration.load((Bootstrap) instance);
        BotConfiguration.RedisConfig redisConfig = BotConfiguration.redis;
        if (redisConfig.embed) // start embedded redis server
            BotScheduler.runTask(() -> createAndStartRedisServer(redisConfig.host, redisConfig.port, instance.getDataFolder()));
        JedisUtil.init(redisConfig.host, redisConfig.port);
    }

    @Override
    public void onDisable() {
        JedisUtil.close();
        instance = null;
    }

    private void createAndStartRedisServer(String host, int port, File root) {
        File folder = new File(root, "redis");
        String path = folder.getAbsolutePath() + "/" + EXEC_NAME;
        String exePath = folder.getAbsolutePath() + "/" + EXEC_NAME + ".exe";
        String appPath = folder.getAbsolutePath() + "/" + EXEC_NAME + ".app";

        if (!folder.exists()) {
            folder.mkdirs();
            saveResourceFromJar(folder.getAbsolutePath(), EXEC_NAME);
            saveResourceFromJar(folder.getAbsolutePath(), EXEC_NAME + ".exe");
            saveResourceFromJar(folder.getAbsolutePath(), EXEC_NAME + ".app");
        }

        RedisExecProvider customProvider = RedisExecProvider.defaultProvider()
                .override(OS.UNIX, Architecture.x86, path)
                .override(OS.UNIX, Architecture.x86_64, path)
                .override(OS.WINDOWS, Architecture.x86, exePath)
                .override(OS.WINDOWS, Architecture.x86_64, exePath)
                .override(OS.MAC_OS_X, Architecture.x86, appPath)
                .override(OS.MAC_OS_X, Architecture.x86_64, appPath);
        try {
            this.server = RedisServer.builder()
                    .redisExecProvider(customProvider)
                    .slaveOf(host, port)
                    .port(port)
                    .build();
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    private static void saveResourceFromJar(String targetPath, String name) {
        File file = new File(targetPath, name);
        try (
                InputStream input = RedisServer.class.getClassLoader().getResourceAsStream(name);
                FileOutputStream output = new FileOutputStream(file);
        ) {
            Assert.notNull(input, "Can't find jar resource: " + name);
            byte[] b = new byte[1024];
            int length;
            while ((length = input.read(b)) > 0) {
                output.write(b, 0, length);
            }
        }
        file.setExecutable(true);
    }

}
