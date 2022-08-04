package com.illtamer.infinite.bot.api.message;

import com.illtamer.infinite.bot.api.entity.TransferEntity;
import com.illtamer.infinite.bot.api.entity.transfer.Image;
import com.illtamer.infinite.bot.api.util.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Message 中所有 {@link com.illtamer.infinite.bot.api.entity.TransferEntity} 构成的链表
 * */
public class MessageChain {

    private static final Map<String, Class<? extends TransferEntity>> MAPPER;
    private final List<TransferEntity> transferEntities;

    protected MessageChain() {
        this.transferEntities = new ArrayList<>();
    }

    protected void boltOn(String type, List<Map.Entry<String, Object>> data) {
//        Gson gson = new Gson();
//        return list.stream()
//                .filter(entry -> entity.getName().equals(entry.key))
//                .map(entry -> gson.fromJson(gson.toJson(entry.value), entity))
//                .collect(Collectors.toList());
    }

    static {
        MAPPER = Maps.of("image", Image.class);
    }

}
