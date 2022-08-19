package com.illtamer.infinite.bot.api.message;

import com.google.gson.*;
import com.illtamer.infinite.bot.api.Pair;
import com.illtamer.infinite.bot.api.exception.ExclusiveMessageException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Json 类型消息
 * <p>
 * 用于主动传输消息
 * */
public class JsonMessage extends Message {

    private final JsonArray array;
    private final MessageChain messageChain;

    private boolean textOnly;

    protected JsonMessage() {
        this(new JsonArray(), true);
    }

    private JsonMessage(JsonArray array, boolean textOnly) {
        this.array = array;
        this.textOnly = textOnly;
        this.messageChain = new MessageChain();
    }

    @Override
    @NotNull
    public MessageChain getMessageChain() {
        return messageChain;
    }

    @Override
    @NotNull
    public List<String> getCleanMessage() {
        List<String> list = new ArrayList<>();
        for (JsonElement element : array) {
            JsonObject object = (JsonObject) element;
            if (!"text".equals(object.get("type").getAsString())) continue;
            list.add(object.get("data").getAsJsonObject().get("text").getAsString());
        }
        return list;
    }

    @Override
    public int getSize() {
        return array.size();
    }

    @Override
    public boolean isTextOnly() {
        return textOnly;
    }

    @Override
    public JsonMessage clone() {
        return new JsonMessage(array, textOnly);
    }

    @Override
    public String toString() {
        return array.toString();
    }

    @Override
    protected void add(String type, Map<String, @Nullable Object> data) {
        if (textOnly)
            textOnly = "text".equals(type);
        JsonObject dataJson = new JsonObject();
        final List<Map.Entry<String, Object>> notnull = data.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toList());
        if (notnull.size() == 0) return;
        messageChain.boltOn(type, notnull);

        notnull.forEach(entry -> {
                    Object value = entry.getValue();
                    JsonElement element;
                    if (value instanceof Message)
                        element = ((JsonMessage) value).array;
                    else
                        element = new JsonPrimitive(value.toString());
                    dataJson.add(entry.getKey(), element);
                });
        JsonObject node = new JsonObject();
        node.add("type", new JsonPrimitive(type));
        node.add("data", dataJson);
        array.add(node);
    }

    @Override
    protected void addExclusive(String type, Map<String, @Nullable Object> data) {
        if (!array.isEmpty()) {
            for (JsonElement jsonElement : array) {
                JsonObject object = (JsonObject) jsonElement;
                if (!object.get("type").getAsString().equals(type))
                    throw new ExclusiveMessageException(type);
            }
        }
        add(type, data);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<Pair<String, Map<String, @NotNull Object>>> entryList() {
        final Gson gson = new Gson();
        List<Pair<String, Map<String, @NotNull Object>>> list = new ArrayList<>(array.size());
        for (JsonElement element : array) {
            JsonObject node = (JsonObject) element;
            final String type = node.get("type").getAsString();
            final Map<String, Object> data = gson.fromJson(node.get("data"), Map.class);
            list.add(new Pair<>(type, data));
        }
        return list;
    }

}
