package com.illtamer.infinite.bot.minecraft.start.bungee;

import com.illtamer.infinite.bot.minecraft.api.BotScheduler;
import com.illtamer.infinite.bot.minecraft.api.adapter.Bootstrap;
import com.illtamer.infinite.bot.minecraft.api.adapter.Configuration;
import com.illtamer.infinite.bot.minecraft.configuration.BotNettyHolder;
import com.illtamer.infinite.bot.minecraft.configuration.EmbedRedisStarter;
import com.illtamer.infinite.bot.minecraft.configuration.StatusCheckRunner;
import com.illtamer.infinite.bot.minecraft.listener.BungeeCommandListener;
import com.illtamer.infinite.bot.minecraft.util.ExpansionUtil;
import com.illtamer.infinite.bot.minecraft.util.JedisUtil;

import java.io.InputStream;

public class BungeeBootstrap extends EmbedRedisStarter implements Bootstrap {

    private BotNettyHolder nettyHolder;

    @Override
    public void onLoad() {
        nettyHolder = new BotNettyHolder(getLogger(), JedisUtil::publish);
        super.onLoad();
        nettyHolder.connect();
    }

    @Override
    public void onEnable() {
        nettyHolder.checkConnection();
        BotScheduler.runTaskTimer(new StatusCheckRunner(getLogger()), 15, 30);
        getProxy().getPluginManager().registerCommand(this, new BungeeCommandListener());
        getLogger().info("Bungee 模式已启动，暂不支持加载附属");
    }

    @Override
    public void onDisable() {
        BotScheduler.close();
        super.onDisable();
        nettyHolder.close();
    }

    @Override
    public void saveResource(String fileName, boolean replace) {
        ExpansionUtil.savePluginResource(fileName, replace, getDataFolder(), this::getResource);
    }

    @Override
    public Configuration createConfig() {
        return new BungeeConfigSection.Config();
    }

    @Override
    public InputStream getResource(String fileName) {
        return getResourceAsStream(fileName);
    }

    @Override
    public Type getType() {
        return Type.BUNGEE;
    }

    public static BungeeBootstrap getInstance() {
        return (BungeeBootstrap) EmbedRedisStarter.instance;
    }

}
