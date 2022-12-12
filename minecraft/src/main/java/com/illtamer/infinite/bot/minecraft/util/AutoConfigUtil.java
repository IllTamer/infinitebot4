package com.illtamer.infinite.bot.minecraft.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@UtilityClass
public class AutoConfigUtil {

    private static final Map<Class<?>, Function<String, Object>> DEFAULT_CAST_MAP = new HashMap<>();

    static {
        DEFAULT_CAST_MAP.put(String.class, s -> s);
        DEFAULT_CAST_MAP.put(Integer.class, Integer::parseInt);
        DEFAULT_CAST_MAP.put(Long.class, Long::parseLong);
        DEFAULT_CAST_MAP.put(Float.class, Float::parseFloat);
        DEFAULT_CAST_MAP.put(Double.class, Double::parseDouble);
        DEFAULT_CAST_MAP.put(Byte.class, Byte::parseByte);
        DEFAULT_CAST_MAP.put(Short.class, Short::parseShort);
    }

    @Nullable
    public static Object castDefaultBasicType(String defaultValue, Class<?> fieldType) {
        for (Map.Entry<Class<?>, Function<String, Object>> entry : DEFAULT_CAST_MAP.entrySet()) {
            if (entry.getKey().equals(fieldType)) {
                try {
                    return entry.getValue().apply(defaultValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return null;
    }

}
