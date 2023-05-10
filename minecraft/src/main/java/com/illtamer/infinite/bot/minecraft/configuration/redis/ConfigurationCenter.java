package com.illtamer.infinite.bot.minecraft.configuration.redis;

import com.illtamer.infinite.bot.minecraft.api.event.LocalEvent;
import com.illtamer.infinite.bot.minecraft.configuration.config.BotConfiguration;

/**
 * 配置中心
 * */
public class ConfigurationCenter {

    public static final String IDENTIFY = "infinite-bot-3_configuration-center";

    /**
     * 配置更新触发
     * */
    protected static void update(LocalEvent event) {
        BotConfiguration.getInstance().loadConfigurations(event.getData());
    }

}
