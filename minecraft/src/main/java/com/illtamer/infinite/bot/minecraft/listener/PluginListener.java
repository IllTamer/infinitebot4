package com.illtamer.infinite.bot.minecraft.listener;

import com.illtamer.infinite.bot.minecraft.api.IExternalExpansion;
import com.illtamer.infinite.bot.minecraft.expansion.ExpansionLoader;
import com.illtamer.infinite.bot.minecraft.start.bukkit.BukkitBootstrap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class PluginListener implements Listener {

    private final ExpansionLoader expansionLoader;
    private final Logger logger;

    public PluginListener(BukkitBootstrap instance) {
        this.expansionLoader = instance.getExpansionLoader();
        this.logger = instance.getLogger();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDisabled(PluginDisableEvent event) {
        final Plugin plugin = event.getPlugin();
        final IExternalExpansion externalExpansion = expansionLoader.getExternalExpansion(plugin);
        if (externalExpansion != null) {
            logger.warning("插件 " + plugin.getName() + " 在卸载时未主动注销附属，被动注销中...");
            expansionLoader.disableExternalExpansion(externalExpansion);
        }
    }

}
