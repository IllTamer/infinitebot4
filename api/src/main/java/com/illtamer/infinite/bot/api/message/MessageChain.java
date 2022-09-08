package com.illtamer.infinite.bot.api.message;

import com.google.gson.Gson;
import com.illtamer.infinite.bot.api.entity.TransferEntity;
import com.illtamer.infinite.bot.api.entity.transfer.*;
import lombok.Getter;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Message 中所有 {@link com.illtamer.infinite.bot.api.entity.TransferEntity} 构成的链表
 * */
public class MessageChain {

    private static final Map<String, Class<? extends TransferEntity>> MAPPER;
    @Getter
    private final List<TransferEntity> entities;
    private final Gson gson;

    protected MessageChain() {
        this.entities = new ArrayList<>();
        this.gson = new Gson();
    }

    protected void boltOn(String type, List<Map.Entry<String, Object>> data) {
        Class<? extends TransferEntity> clazz = MAPPER.get(type);
        if (clazz == null)
            clazz = Unknown.class;
        try {
            final Map<String, Object> objectMap = data.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            entities.add(gson.fromJson(gson.toJson(objectMap), clazz));
        } catch (Exception e) {
            System.err.println("Unknown error when transferring type: " + type);
            e.printStackTrace();
        }
    }

    protected void removeWith(Predicate<TransferEntity> predicate, Consumer<Integer> consumer) {
        final Iterator<TransferEntity> each = entities.iterator();
        int index = 0;
        while (each.hasNext()) {
            final TransferEntity entity = each.next();
            if (predicate.test(entity)) {
                each.remove();
                consumer.accept(index ++);
            }
        }
    }

    @Override
    public String toString() {
        return "MessageChain{" +
                "entities=" + entities +
                '}';
    }

    // TODO 更多消息类型支持
    static {
        HashMap<String, Class<? extends TransferEntity>> map = new HashMap<>();
        map.put(At.class.getSimpleName().toLowerCase(), At.class);
        map.put(Face.class.getSimpleName().toLowerCase(), Face.class);
        map.put(Forward.class.getSimpleName().toLowerCase(), Forward.class);
        map.put(Image.class.getSimpleName().toLowerCase(), Image.class);
        map.put(Record.class.getSimpleName().toLowerCase(), Record.class);
        map.put(Redbag.class.getSimpleName().toLowerCase(), Redbag.class);
        map.put(Reply.class.getSimpleName().toLowerCase(), Reply.class);
        map.put(Share.class.getSimpleName().toLowerCase(), Share.class);
        map.put(Text.class.getSimpleName().toLowerCase(), Text.class);
        map.put(Video.class.getSimpleName().toLowerCase(), Video.class);
        MAPPER = map;
    }

}
