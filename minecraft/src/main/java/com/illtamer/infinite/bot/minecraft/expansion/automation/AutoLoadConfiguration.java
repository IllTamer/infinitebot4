package com.illtamer.infinite.bot.minecraft.expansion.automation;

import com.illtamer.infinite.bot.api.util.Assert;
import com.illtamer.infinite.bot.minecraft.Bootstrap;
import com.illtamer.infinite.bot.minecraft.api.IExpansion;
import com.illtamer.infinite.bot.minecraft.expansion.automation.annotation.ConfigClass;
import com.illtamer.infinite.bot.minecraft.expansion.automation.annotation.ConfigField;
import com.illtamer.infinite.bot.minecraft.expansion.ExpansionConfig;
import com.illtamer.infinite.bot.minecraft.util.AutoConfigUtil;
import lombok.SneakyThrows;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 自动配置类
 * @see #doDeserialize(Map, Class, IExpansion)
 * @see Registration
 * */
public abstract class AutoLoadConfiguration implements ConfigurationSerializable {

    // default config filed
    protected int version;

    private final IExpansion expansion;
    private final ExpansionConfig configFile;
    private final List<Field> fieldList;

    public AutoLoadConfiguration(IExpansion expansion) {
        this(0, expansion);
    }

    public AutoLoadConfiguration(int version, IExpansion expansion) {
        this.version = version;
        this.expansion = expansion;
        this.fieldList = new ArrayList<>();
        String fileName = loadConfigFieldsAndGetFileName();
        this.configFile = new ExpansionConfig(fileName, expansion, version);
        loadConfigDataToField();
    }

    @NotNull
    @SneakyThrows
    private String loadConfigFieldsAndGetFileName() {
        final Class<? extends AutoLoadConfiguration> clazz = this.getClass();
        final ConfigClass configClass = clazz.getAnnotation(ConfigClass.class);
        Assert.notNull(configClass, "Lack of necessary annotation 'ConfigClass'");
        fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if (fieldList.stream().noneMatch(field -> field.getName().equals("version"))) {
            fieldList.add(this.getClass().getSuperclass().getDeclaredField("version"));
        }
        fieldList.forEach(field -> field.setAccessible(true));
        return configClass.name();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        for (Field field : fieldList) {
            String key = getFieldRef(field);
            try {
                map.put(key, field.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public ExpansionConfig getConfigFile() {
        return configFile;
    }

    protected IExpansion getExpansion() {
        return expansion;
    }

    protected List<Field> getFieldList() {
        return fieldList;
    }

    @SneakyThrows
    private void loadConfigDataToField() {
        final FileConfiguration config = configFile.getConfig();
        for (Field field : fieldList) {
            String key = getFieldRef(field);
            Object value = config.get(key);
            final Class<?> fieldType = field.getType();
            if (value != null) {
                try {
                    // special field type
                    if (Map.class.isAssignableFrom(fieldType)) {
                        Assert.isTrue(value instanceof MemorySection, "Map类型字段配置节点下须有子项！");
                        value = ((MemorySection) value).getValues(false);
                    } else if (Enum.class.isAssignableFrom(fieldType)) {
                        String[] methods = new String[] {"parse", "get" + fieldType.getSimpleName(), "valueOf"};
                        for (String methodName : methods) {
                            Method method;
                            try {
                                method = fieldType.getDeclaredMethod(methodName, String.class);
                            } catch (NoSuchMethodException e) {
                                continue;
                            }
                            Assert.isTrue(Modifier.isStatic(method.getModifiers()), "Enum cast method must be static!");
                            method.setAccessible(true);
                            value = method.invoke(null, String.valueOf(value));
                            break;
                        }
                    }
                    field.set(this, value);
                } catch (Exception e) {
                    Bootstrap.getInstance().getLogger().warning("字段 " + key + " 赋值出错，请检查！");
                    e.printStackTrace();
                }
                continue;
            }
            final ConfigField configField = field.getAnnotation(ConfigField.class);
            if (configField == null) continue;
            final String defaultValue = configField.value();
            if (defaultValue.length() == 0) continue;
            // cast support value
            field.set(this, AutoConfigUtil.castDefaultBasicType(defaultValue, fieldType));
        }
    }

    /**
     * 帮助使用者快捷注册静态方法 deserialize / valueOf 的实现
     * <pre>
     *     public static TestConfig deserialize(Map<String, Object> map) {
     *         return AutoLoadConfiguration.doDeserialize(map, TestConfig.class, expansion);
     *     }
     * </pre>
     * */
    @SneakyThrows
    public static <T extends AutoLoadConfiguration> T doDeserialize(Map<String, Object> map, Class<T> clazz, IExpansion expansion) {
        return doDeserialize(map, clazz, 0, expansion);
    }

    @SneakyThrows
    public static <T extends AutoLoadConfiguration> T doDeserialize(Map<String, Object> map, Class<T> clazz, int version, IExpansion expansion) {
        // TODO 多级下 map 的结构?
        System.out.println("deserialize map:\n" + map);
        final Constructor<T> constructor = clazz.getConstructor(int.class, IExpansion.class);
        final T instance = constructor.newInstance(version, expansion);
        for (Field field : instance.getFieldList()) {
            final Object value = map.get(getFieldRef(field));
            if (value != null)
                field.set(instance, value);
        }
        return instance;
    }


    /**
     * @apiNote 目标字段应使用驼峰命名法
     * */
    @NotNull
    private static String getFieldRef(Field field) {
        String key = null;
        final ConfigField configField = field.getAnnotation(ConfigField.class);
        if (configField != null) {
            key = configField.ref();
        }
        if (key == null || key.length() == 0) {
            StringBuilder builder = new StringBuilder();
            for (char c : field.getName().toCharArray()) {
                if (65 <= c && c <= 90)
                    builder.append('-').append((char) (c+32));
                else
                    builder.append(c);
            }
            return builder.toString();
        }
        return key;
    }

}
