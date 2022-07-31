package com.illtamer.infinite.bot.api.message;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class CQMessage extends Message {

    @Override
    protected void add(String type, Map<String, @Nullable Object> data) {

    }

    @Override
    protected void addExclusive(String type, Map<String, @Nullable Object> data) {

    }

    @Override
    public boolean isTextOnly() {
        return false;
    }

    @Override
    public Message clone() {
        return null;
    }

    @Override
    public String toString() {
        return null;
    }

}
