package com.illtamer.infinite.bot.minecraft.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public final class ReflectionUtil {

    @NotNull
    public static Field findField(@NotNull Class<?> type, @NotNull String fieldName) throws NoSuchFieldException {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field '" + fieldName + "' not found in " + type.getName());
    }

}
