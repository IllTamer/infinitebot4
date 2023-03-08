package com.illtamer.infinite.bot.minecraft.start.bukkit;

import com.illtamer.infinite.bot.minecraft.api.EventExecutor;
import com.illtamer.infinite.bot.minecraft.api.adapter.Bootstrap;
import com.illtamer.infinite.bot.minecraft.api.adapter.Configuration;
import com.illtamer.infinite.bot.minecraft.configuration.BotNettyHolder;
import com.illtamer.infinite.bot.minecraft.configuration.StatusCheckRunner;
import com.illtamer.infinite.bot.minecraft.configuration.config.BotConfiguration;
import com.illtamer.infinite.bot.minecraft.expansion.ExpansionLoader;
import com.illtamer.infinite.bot.minecraft.listener.BukkitCommandListener;
import com.illtamer.infinite.bot.minecraft.listener.PluginListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

// TODO 集中help管理、command支持
//      libs folder
public class BukkitBootstrap extends JavaPlugin implements Bootstrap {

    @Getter
    private static BukkitBootstrap instance;

    @Getter
    private final ExpansionLoader expansionLoader = new ExpansionLoader(this);

    private BotNettyHolder nettyHolder;

    @Override
    public void onLoad() {
        // TODO check bungee mode and change handler method
        this.nettyHolder = new BotNettyHolder(getLogger(), EventExecutor::dispatchListener);
        BotConfiguration.load(instance = this);
        nettyHolder.connect();
    }

    @Override
    public void onEnable() {
        nettyHolder.checkConnection();
        Bukkit.getScheduler().runTaskTimerAsynchronously(instance,
                new StatusCheckRunner(instance), 15 * 20, 30 * 20);
        expansionLoader.loadExpansions(false);
        BukkitCommandListener bukkitCommandListener = new BukkitCommandListener();
        final PluginCommand command = Optional.ofNullable(getServer().getPluginCommand("InfiniteBot3"))
                .orElseThrow(NullPointerException::new);
        command.setTabCompleter(bukkitCommandListener);
        command.setExecutor(bukkitCommandListener);
        getServer().getPluginManager().registerEvents(new PluginListener(this), this);
    }

    @Override
    public void onDisable() {
        expansionLoader.disableExpansions(false);
        BotConfiguration.saveAndClose();
        nettyHolder.close();
    }

    @Override
    public Configuration createConfig() {
        return new BukkitConfigSection.Config();
    }

    @Override
    public Type getType() {
        return Type.BUKKIT;
    }

}
