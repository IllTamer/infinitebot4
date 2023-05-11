package com.illtamer.infinite.bot.api.event;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.illtamer.infinite.bot.api.annotation.Coordinates;
import com.illtamer.infinite.bot.api.config.CQHttpWebSocketConfiguration;
import com.illtamer.infinite.bot.api.message.DateTypeAdapter;
import com.illtamer.infinite.bot.api.message.Message;
import com.illtamer.infinite.bot.api.message.MessageTypeAdapter;
import com.illtamer.infinite.bot.api.message.ObjectTypeAdapter;
import com.illtamer.infinite.bot.api.util.ClassUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EventResolver {

    private static final LayerEventTree<Event> root = new LayerEventTree<>(Event.class);

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateTypeAdapter())
            .registerTypeAdapter(Message.class, new MessageTypeAdapter())
            .registerTypeAdapter(new TypeToken<Map<String, Object>>(){}.getType(),new ObjectTypeAdapter())
            .create();

    static {
        init();
    }

    public static Event dispatchEvent(JsonObject json) {
        String postType = json.get(Coordinates.POST_TYPE).getAsString();
        Coordinates.PostType type = Coordinates.PostType.format(postType);
        String secTypeName = type.parseSecType();
        String secType = json.get(secTypeName).getAsString();

        String subType = Optional.ofNullable(json.get(Coordinates.SUB_TYPE)).orElse(new JsonPrimitive("")).getAsString();
        String index = constructIndex(postType, secType, subType);
        final Class<? extends Event> clazz = root.get(index);
        return GSON.fromJson(json, clazz);
    }

    @SuppressWarnings("unchecked")
    private static void init() {
        ClassLoader classLoader = CQHttpWebSocketConfiguration.class.getClassLoader();
        List<Class<?>> classes = ClassUtil.getClasses("com.illtamer.infinite.bot.api.event", classLoader);
        classes.stream()
                .filter(clazz -> clazz.isAnnotationPresent(Coordinates.class))
                .forEach(clazz -> {
                    Coordinates coordinates = clazz.getAnnotation(Coordinates.class);
                    root.add(coordinates, (Class<? extends Event>) clazz);
                });
    }

    private static String constructIndex(String... args) {
        StringBuilder builder = new StringBuilder();
        for (String arg : args)
            if (arg != null && arg.length() != 0)
                builder.append('.').append(arg);
        builder.deleteCharAt(0);
        return builder.toString();
    }

}
