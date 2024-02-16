package com.illtamer.infinite.bot.minecraft.start.origin;

import com.illtamer.infinite.bot.minecraft.api.adapter.Bootstrap;
import com.illtamer.infinite.bot.minecraft.api.adapter.Configuration;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

// TODO
public class OriginBootstrap implements Bootstrap {

    @Override
    public void saveResource(String fileName, boolean replace) {

    }

    @Override
    public Configuration createConfig() {
        return null;
    }

    @Override
    public File getDataFolder() {
        return null;
    }

    @Override
    public Logger getLogger() {
        return null;
    }

    @Override
    public InputStream getResource(String fileName) {
        return null;
    }

    @Override
    public Type getType() {
        return Type.ORIGIN;
    }

    public static void main(String[] args) {
        // TODO

    }

}
