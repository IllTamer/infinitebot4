package com.illtamer.infinite.bot.minecraft.start.bukkit;

import com.illtamer.infinite.bot.minecraft.api.BotScheduler;
import com.illtamer.infinite.bot.minecraft.api.EventExecutor;
import com.illtamer.infinite.bot.minecraft.api.StaticAPI;
import com.illtamer.infinite.bot.minecraft.api.adapter.Bootstrap;
import com.illtamer.infinite.bot.minecraft.api.adapter.Configuration;
import com.illtamer.infinite.bot.minecraft.configuration.BotNettyHolder;
import com.illtamer.infinite.bot.minecraft.configuration.StatusCheckRunner;
import com.illtamer.infinite.bot.minecraft.configuration.config.BotConfiguration;
import com.illtamer.infinite.bot.minecraft.expansion.ExpansionLoader;
import com.illtamer.infinite.bot.minecraft.listener.BukkitCommandListener;
import com.illtamer.infinite.bot.minecraft.listener.PluginListener;
import com.illtamer.infinite.bot.minecraft.util.Optional;
import lombok.Getter;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

// TODO 集中help管理、command支持
//      libs folder
public class BukkitBootstrap extends JavaPlugin implements Bootstrap {

    @Getter
    private static BukkitBootstrap instance;

    @Getter
    private final ExpansionLoader expansionLoader = new ExpansionLoader(this);
    @Getter
    private final Optional<BotNettyHolder> nettyHolder = Optional.empty();

    @Override
    public void onLoad() {
        StaticAPI.setInstance(instance = this);
//        DependencyLoader.load(instance);
        BotConfiguration.load(instance);
        this.nettyHolder.set(new BotNettyHolder(getLogger(), EventExecutor::dispatchListener));
        nettyHolder.get().connect();
    }

    @Override
    public void onEnable() {
        nettyHolder.ifPresent(BotNettyHolder::checkConnection);
        BotScheduler.runTaskTimer(new StatusCheckRunner(getLogger()), 15, 30);
        expansionLoader.loadExpansions(false);
        BukkitCommandListener bukkitCommandListener = new BukkitCommandListener();
        final PluginCommand command = Optional.ofNullable(getServer().getPluginCommand("InfiniteBot4"))
                .orElseThrow(NullPointerException::new);
        command.setTabCompleter(bukkitCommandListener);
        command.setExecutor(bukkitCommandListener);
        getServer().getPluginManager().registerEvents(new PluginListener(this), this);
    }

    @Override
    public void onDisable() {
        BotScheduler.close();
        expansionLoader.disableExpansions(false);
        BotConfiguration.saveAndClose();
        nettyHolder.ifPresent(BotNettyHolder::close);
        instance = null;
    }

    @Override
    public Configuration createConfig() {
        return new BukkitConfigSection.Config();
    }

    @Override
    public ClassLoader getInstClassLoader() {
        return getClassLoader();
    }

    @Override
    public Type getType() {
        return Type.BUKKIT;
    }

}
