package com.illtamer.infinite.bot.minecraft;

import com.illtamer.infinite.bot.minecraft.configuration.BotNettyStarter;
import com.illtamer.infinite.bot.minecraft.configuration.StatusCheckRunner;
import com.illtamer.infinite.bot.minecraft.configuration.config.BotConfiguration;
import com.illtamer.infinite.bot.minecraft.expansion.ExpansionLoader;
import com.illtamer.infinite.bot.minecraft.listener.CommandListener;
import com.illtamer.infinite.bot.minecraft.util.PluginUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;

import java.util.Optional;

// TODO 集中help管理
public class Bootstrap extends BotNettyStarter {

    @Getter
    private final ExpansionLoader expansionLoader = new ExpansionLoader(this);

    @Override
    public void onEnable() {
        checkConnection();
        Bukkit.getScheduler().runTaskTimerAsynchronously(instance,
                new StatusCheckRunner(instance), 15 * 20, 30 * 20);
        expansionLoader.loadExpansions();
        CommandListener commandListener = new CommandListener();
        final PluginCommand command = Optional.ofNullable(getServer().getPluginCommand("InfiniteBot3"))
                .orElseThrow(NullPointerException::new);
        command.setTabCompleter(commandListener);
        command.setExecutor(commandListener);
    }

    @Override
    public void onDisable() {
        expansionLoader.disableExpansions();
        BotConfiguration.saveAndClose();
        instance = null;
        close();
    }

    public static Bootstrap getInstance() {
        return (Bootstrap) instance;
    }

}
