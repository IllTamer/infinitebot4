package com.illtamer.infinite.bot.api.message;

import com.illtamer.infinite.bot.api.exception.ExclusiveMessageException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CQ Code 消息
 * <p>
 * 用于被动接收消息
 * */
public class CQMessage extends Message {

    private final List<Entry> list;
    private final MessageChain messageChain;

    private boolean textOnly;

    public CQMessage() {
        this(new ArrayList<>(), true);
    }

    private CQMessage(List<Entry> list, boolean textOnly) {
        this.list = list;
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
        return list.stream()
                .filter(entry -> "text".equals(entry.key))
                .map(entry -> (String) entry.value.get("text"))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isTextOnly() {
        return textOnly;
    }

    @Override
    public Message clone() {
        return new CQMessage(list, textOnly);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Entry entry : list) {
            if ("text".equals(entry.key))
                builder.append((String) entry.value.get("text"));
            else {
                builder.append("[CQ:").append(entry.key);
                for (Map.Entry<String, Object> dataEntry : entry.value.entrySet())
                    builder.append(',').append(dataEntry.getKey()).append((String) dataEntry.getValue());
                builder.append(']');
            }
        }
        return builder.toString();
    }

    @Override
    protected void add(String type, Map<String, @Nullable Object> data) {
        if (textOnly)
            textOnly = "text".equals(type);
        Entry entryObject = new Entry(type);
        final List<Map.Entry<String, Object>> notnull = data.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toList());
        if (notnull.size() == 0) return;
        messageChain.boltOn(type, notnull);

        notnull.forEach(entry -> {
                    Object value = entry.getValue();
                    Object element;
                    if (value instanceof Message)
                        element = ((CQMessage) value).list;
                    else
                        element = value.toString();
                    entryObject.putValue(entry.getKey(), element);
                });
        list.add(entryObject);
    }

    @Override
    protected void addExclusive(String type, Map<String, @Nullable Object> data) {
        if (!list.isEmpty()) {
            for (Entry entry : list) {
                if (!entry.getKey().equals(type))
                    throw new ExclusiveMessageException(type);
            }
        }
        add(type, data);
    }

    public static class Entry implements Map.Entry<String, Map<String, Object>> {

        private final String key;
        private final Map<String, Object> value;

        private Entry(String key) {
            this.key = key;
            this.value = new LinkedHashMap<>();
        }


        @Override
        public String getKey() {
            return key;
        }

        public void putValue(String vKey, Object vValue) {
            value.put(vKey, vValue);
        }

        @Override
        public Map<String, Object> getValue() {
            return value;
        }

        @Override
        public Map<String, Object> setValue(Map<String, Object> value) {
            throw new UnsupportedOperationException();
        }

    }

}
