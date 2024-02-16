package com.illtamer.infinite.bot.minecraft.start.origin;

import com.illtamer.infinite.bot.minecraft.api.adapter.Configuration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

// TODO
class OriginConfig implements Configuration {

    @Override
    public String getString(String path) {
        return null;
    }

    @Override
    public String getString(String path, String def) {
        return null;
    }

    @Override
    public List<String> getStringList(String path) {
        return null;
    }

    @Override
    public Integer getInt(String path) {
        return null;
    }

    @Override
    public Integer getInt(String path, Integer def) {
        return null;
    }

    @Override
    public Long getLong(String path) {
        return null;
    }

    @Override
    public Long getLong(String path, Long def) {
        return null;
    }

    @Override
    public Boolean getBoolean(String path) {
        return null;
    }

    @Override
    public Boolean getBoolean(String path, Boolean def) {
        return null;
    }

    @Override
    public List<Long> getLongList(String path) {
        return null;
    }

    @Override
    public void set(String path, Object value) {

    }

    @Nullable
    @Override
    public Configuration getSection(String path) {
        return null;
    }

    @Override
    public Configuration createSection(String path, Map<String, Object> data) {
        return null;
    }

    @Override
    public String saveToString() {
        return null;
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return null;
    }

    @Override
    public Map<String, Object> getValues(boolean deep) {
        return null;
    }

    @Override
    public void save(File file) throws IOException {

    }

    @Override
    public void load(File file) throws IOException {

    }

    @Override
    public void load(String yaml) {

    }

}
