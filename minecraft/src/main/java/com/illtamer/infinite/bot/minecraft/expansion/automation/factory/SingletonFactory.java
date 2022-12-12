package com.illtamer.infinite.bot.minecraft.expansion.automation.factory;

import com.illtamer.infinite.bot.minecraft.exception.InitializationException;
import org.jetbrains.annotations.NotNull;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * 单例工厂
 * */
public class SingletonFactory {

    private static final HashMap<Class<?>, Object> SINGLETONS = new HashMap<>(1 << 6);

    private SingletonFactory() {}

    /**
     * 获取单例对象
     * @apiNote 传入类必须有无参构造或已被注册
     * */
    @NotNull
    public static <T> T getInstance(Class<T> clazz) {
        if (clazz == null)
            throw new NullPointerException();

        Object instance = SINGLETONS.get(clazz);
        if (instance != null)
            return clazz.cast(instance);

        instance = createInstance(clazz);
        return clazz.cast(instance);
    }

    /**
     * 设置单例对象
     * */
    public static void setInstance(Class<?> clazz, Object object) {
        if (!clazz.isInstance(object))
            throw new InitializationException("Mismatched class and instance");
        if (SINGLETONS.containsKey(clazz))
            throw new KeyAlreadyExistsException();
        SINGLETONS.put(clazz, object);
    }

    /**
     * 移除单例
     * */
    public static <T> T remove(Class<T> clazz) {
        return clazz.cast(SINGLETONS.remove(clazz));
    }

    @NotNull
    private synchronized static Object createInstance(Class<?> clazz) {
        Object instance = SINGLETONS.get(clazz);
        if (instance != null) return instance;
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            instance = constructor.newInstance();
            SINGLETONS.put(clazz, instance);
            return instance;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new InitializationException(e);
        }
    }

}
