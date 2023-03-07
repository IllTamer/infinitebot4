package com.illtamer.infinite.bot.minecraft.api.adapter;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ConfigSection {

    String getString(String path);

    String getString(String path, String def);

    List<String> getStringList(String path);

    Integer getInt(String path);

    Integer getInt(String path, Integer def);

    Long getLong(String path);

    Long getLong(String path, Long def);

    Boolean getBoolean(String path);

    Boolean getBoolean(String path, Boolean def);

    List<Long> getLongList(String path);

    void set(String path, Object value);

    Set<String> getKeys(boolean deep);

    Map<String, Object> getValues(boolean deep);

}
