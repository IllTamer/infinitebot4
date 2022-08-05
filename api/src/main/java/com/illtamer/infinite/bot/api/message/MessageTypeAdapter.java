package com.illtamer.infinite.bot.api.message;

import com.google.gson.*;
import com.illtamer.infinite.bot.api.util.Assert;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Gson {@link Message} TypeAdapter
 * */
public class MessageTypeAdapter implements JsonSerializer<Message>, JsonDeserializer<Message> {

    @Override
    public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final String cqJson = json.getAsString();
        final char[] chars = cqJson.toCharArray();
        final MessageBuilder builder = MessageBuilder.cq();
        for (int i = 0; i < chars.length; ++ i) {
            if (chars[i] == '[') {
                for (int j = i; j < chars.length; ++ j) {
                    if (chars[j] == ']') {
                        doDeserialize(cqJson.substring(i+1, j), builder, false);
                        i = j;
                        break;
                    }
                }
            } else {
                for (int j = i; j < chars.length; ++ j) {
                    boolean end = false;
                    if (chars[j] == '[' || (end = j+1 == chars.length)) {
                        doDeserialize(end ? cqJson.substring(i, j+1) : cqJson.substring(i, j), builder, true);
                        i = end ? j : j-1;
                        break;
                    }
                }
            }
        }
        return builder.build();
    }

    @Override
    public JsonElement serialize(Message src, Type typeOfSrc, JsonSerializationContext context) {
        Assert.isTrue(src instanceof CQMessage, "Unsupported type");
        return new JsonPrimitive(src.toString());
    }

    protected static void doDeserialize(String cqCode, MessageBuilder builder, boolean property) {
        if (property)
            builder.text(cqCode, false);
        else {
            final String[] params = cqCode.split(",");
            Assert.isTrue(params[0].startsWith("CQ:"), "CQ Code format error");
            Map<String, Object> args = new HashMap<>(params.length-1);
            for (int i = 1; i < params.length; ++ i) {
                final String[] split = params[i].split("=");
                args.put(split[0], split[1]);
            }
            builder.property(params[0].substring(3), args);
        }
    }

}
