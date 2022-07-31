package com.illtamer.infinite.bot.minecraft.api.event;

/**
 * 优先级枚举
 * */
public enum Priority {

    /**
     * 最高 (最先触发)
     * */
    HIGHEST,

    /**
     * 高 (较先触发)
     * */
    HIGH,

    /**
     * 默认 (默认触发)
     * */
    DEFAULT,

    /**
     * 较低 (较后触发)
     * */
    LOW,

    /**
     * 最低 (最后触发)
     * */
    LOWEST

}
