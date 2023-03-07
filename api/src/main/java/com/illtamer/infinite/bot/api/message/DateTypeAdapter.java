package com.illtamer.infinite.bot.api.message;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Date;

public class DateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonElement element, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        String timeStr = element.getAsString();
        long time = Long.parseLong(timeStr);
        if (timeStr.length() == 10)
            time *= 1000;
        return new Date(time);
    }

    @Override
    public JsonElement serialize(Date date, Type type, JsonSerializationContext ctx) {
        return new JsonPrimitive(date.getTime());
    }

}
