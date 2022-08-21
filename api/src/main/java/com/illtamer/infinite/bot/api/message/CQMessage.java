package com.illtamer.infinite.bot.api.message;

import com.illtamer.infinite.bot.api.Pair;
import com.illtamer.infinite.bot.api.exception.ExclusiveMessageException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CQ Code 消息
 * <p>
 * 用于被动接收消息
 * */
public class CQMessage extends Message {

    private final List<Pair<String, Map<String, @NotNull Object>>> list;
    private final MessageChain messageChain;

    private boolean textOnly;

    protected CQMessage() {
        this(new ArrayList<>(), true);
    }

    private CQMessage(List<Pair<String, Map<String, @NotNull Object>>> list, boolean textOnly) {
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
                .filter(entry -> "text".equals(entry.getKey()))
                .map(entry -> (String) entry.getValue().get("text"))
                .collect(Collectors.toList());
    }

    @Override
    public int getSize() {
        return list.size();
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
        for (Pair<String, Map<String, @NotNull Object>> entry : list) {
            if ("text".equals(entry.getKey()))
                builder.append((String) entry.getValue().get("text"));
            else {
                builder.append("[CQ:").append(entry.getKey());
                for (Map.Entry<String, Object> dataEntry : entry.getValue().entrySet())
                    builder.append(',').append(dataEntry.getKey()).append('=').append((String) dataEntry.getValue());
                builder.append(']');
            }
        }
        return builder.toString();
    }

    @Override
    protected void add(String type, Map<String, @Nullable Object> data) {
        if (textOnly)
            textOnly = "text".equals(type);
        Pair<String, Map<String, @NotNull Object>> entryObject = new Pair<>(type, new HashMap<>());
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
                    entryObject.getValue().put(entry.getKey(), element);
                });
        list.add(entryObject);
    }

    @Override
    protected void addExclusive(String type, Map<String, @Nullable Object> data) {
        if (!list.isEmpty()) {
            for (Pair<String, Map<String, @NotNull Object>> entry : list) {
                if (!entry.getKey().equals(type))
                    throw new ExclusiveMessageException(type);
            }
        }
        add(type, data);
    }

    @Override
    protected List<Pair<String, Map<String, @NotNull Object>>> entryList() {
        return list;
    }

}
