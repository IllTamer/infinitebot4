package com.illtamer.infinite.bot.minecraft.start.bungee;

import com.illtamer.infinite.bot.api.event.EventResolver;
import com.illtamer.infinite.bot.minecraft.api.adapter.Bootstrap;
import com.illtamer.infinite.bot.minecraft.api.adapter.Configuration;
import com.illtamer.infinite.bot.minecraft.configuration.BotNettyHolder;
import com.illtamer.infinite.bot.minecraft.configuration.EmbedRedisStarter;
import com.illtamer.infinite.bot.minecraft.listener.BungeeCommandListener;
import com.illtamer.infinite.bot.minecraft.util.ExpansionUtil;
import com.illtamer.infinite.bot.minecraft.util.JedisUtil;

import java.io.InputStream;

public class BungeeBootstrap extends EmbedRedisStarter implements Bootstrap {

    private BotNettyHolder nettyHolder;

    @Override
    public void onLoad() {
        nettyHolder = new BotNettyHolder(getLogger(), event ->
                JedisUtil.publish(EventResolver.GSON.toJson(event)));
        super.onLoad();
        nettyHolder.connect();
    }

    @Override
    public void onEnable() {
        nettyHolder.checkConnection();
        // TODO Status check scheduler
//        Bukkit.getScheduler().runTaskTimerAsynchronously(instance,
//                new StatusCheckRunner(instance), 15 * 20, 30 * 20);
        getProxy().getPluginManager().registerCommand(this, new BungeeCommandListener());
    }

    @Override
    public void onDisable() {
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
