package com.illtamer.infinite.bot.minecraft.api.adapter;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface Configuration extends ConfigSection {

    @Nullable
    ConfigSection getSection(String path);

    ConfigSection createSection(String path, Map<String, Object> data);

    /**
     * 序列化
     * */
    String saveToString();

    /**
     * 将缓存数据保存到磁盘
     * */
    void save(File file) throws IOException;

    /**
     * 将磁盘文件加载到缓存
     * */
    void load(File file) throws IOException;

    /**
     * 从字符串加载
     * */
    void load(String yaml);

}
