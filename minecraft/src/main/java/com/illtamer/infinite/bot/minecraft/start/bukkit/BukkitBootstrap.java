package com.illtamer.infinite.bot.minecraft.start.bukkit;

import com.illtamer.infinite.bot.minecraft.api.adapter.Bootstrap;
import com.illtamer.infinite.bot.minecraft.api.adapter.Configuration;
import com.illtamer.infinite.bot.minecraft.configuration.BotNettyStarter;
import com.illtamer.infinite.bot.minecraft.configuration.StatusCheckRunner;
import com.illtamer.infinite.bot.minecraft.configuration.config.BotConfiguration;
import com.illtamer.infinite.bot.minecraft.expansion.ExpansionLoader;
import com.illtamer.infinite.bot.minecraft.listener.CommandListener;
import com.illtamer.infinite.bot.minecraft.listener.PluginListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;

import java.util.Optional;

// TODO 集中help管理、command支持
//      libs folder
public class BukkitBootstrap extends BotNettyStarter implements Bootstrap {

    @Getter
    private final ExpansionLoader expansionLoader = new ExpansionLoader(this);

    @Override
    public void onEnable() {
        checkConnection();
        Bukkit.getScheduler().runTaskTimerAsynchronously(instance,
                new StatusCheckRunner(instance), 15 * 20, 30 * 20);
        expansionLoader.loadExpansions(false);
        CommandListener commandListener = new CommandListener();
        final PluginCommand command = Optional.ofNullable(getServer().getPluginCommand("InfiniteBot3"))
                .orElseThrow(NullPointerException::new);
        command.setTabCompleter(commandListener);
        command.setExecutor(commandListener);
        getServer().getPluginManager().registerEvents(new PluginListener(this), this);
    }

    @Override
    public void onDisable() {
        close();
        expansionLoader.disableExpansions(false);
        BotConfiguration.saveAndClose();
        instance = null;
    }

    public static BukkitBootstrap getInstance() {
        return (BukkitBootstrap) instance;
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
