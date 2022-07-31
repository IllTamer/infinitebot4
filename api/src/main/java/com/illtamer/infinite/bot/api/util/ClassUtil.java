package com.illtamer.infinite.bot.api.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@UtilityClass
public class ClassUtil {

    @Nullable
    public static Field getDeepField(String name, Class<?> clazz) {
        final List<Field> deepFields = getDeepFields(clazz);
        final List<Field> collect = deepFields.stream().filter(field -> field.getName().equals(name)).collect(Collectors.toList());
        return collect.size() > 0 ? collect.get(0) : null;
    }

    /**
     * 获取当前类及其父类所有成员变量
     * */
    @NotNull
    public static List<Field> getDeepFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null){
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fieldList;
    }

    /**
     * 在已有类实例的前提下获取带注解的方法
     * @param obj 类实例
     * @param annotation 接口的类的Class对象
     * */
    public static HashMap<Method, Annotation> getMethods(Object obj, Class<? extends Annotation> annotation) {
        HashMap<Method, Annotation> hashMap = new HashMap<>(5);
        for (Method method : obj.getClass().getMethods())
            for (Annotation temp : method.getAnnotations())
                if (temp.annotationType() == annotation)
                    hashMap.put(method, temp);
        return hashMap;
    }

    public static HashMap<Class<?>, HashMap<Method, Annotation>> getMethods(List<Class<?>> classes, Class<? extends Annotation> annotation) {
        HashMap<Class<?>, HashMap<Method, Annotation>> map = new HashMap<>(4);
        classes.forEach(c -> {
            HashMap<Method, Annotation> methods = new HashMap<>(3);
            for (Method method : c.getMethods()) {
                for (Annotation temp : method.getAnnotations()) {
                    if (temp.annotationType() == annotation) {
                        methods.put(method, temp);
                    }
                }
            }
            if (methods.size() != 0) {
                map.put(c, methods);
            }
        });
        return map;
    }

    @NotNull
    @SneakyThrows(IOException.class)
    public static List<Class<?>> getClasses(String packageName, ClassLoader classLoader) {
        List<Class<?>> classes = new ArrayList<>();
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs = classLoader.getResources(packageDirName);
        while (dirs.hasMoreElements()) {
            URL url = dirs.nextElement();
            String protocol = url.getProtocol();
            if ("file".equals(protocol)) {
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                classes.addAll(findClassWithDirectory(packageName, filePath, classLoader));
            } else if ("jar".equals(protocol)) {
                classes.addAll(findClassWithJar(packageName, url, classLoader));
            }
        }
        return classes;
    }

    @NotNull
    public static List<Class<?>> findClassWithDirectory(String packageName, String packagePath, ClassLoader classLoader) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory())
            return new ArrayList<>(0);
        File[] dirs = dir.listFiles();
        if (dirs == null)
            return new ArrayList<>(0);

        List<Class<?>> classes = new ArrayList<>();
        // 循环所有文件
        for (File file : dirs) {
            if (file.isDirectory()) {
                classes.addAll(findClassWithDirectory(packageName + "." + file.getName(),
                        file.getAbsolutePath(), classLoader));
            } else if (file.getName().endsWith(".class")) {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(Class.forName(packageName + '.' + className, true, classLoader));
                }
                catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return classes;
    }

    @NotNull
    @SneakyThrows(IOException.class)
    public static List<Class<?>> findClassWithJar(String packageName, URL url, ClassLoader classLoader) {
        List<Class<?>> classes = new ArrayList<>();
        String packageDirName = packageName.replace('.', '/');
        JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
            JarEntry entry = entries.nextElement();
            if (entry.isDirectory()) continue;

            String name = entry.getName();
            if (name.charAt(0) == '/') {
                name = name.substring(1);
            }

            // 如果前半部分和定义的包名相同
            if (name.startsWith(packageDirName) && name.endsWith(".class")) {
                // 去掉后面的".class"
                String className = name.substring(0, name.length() - 6).replace('/', '.');
                try {
                    classes.add(Class.forName(className, true, classLoader));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return classes;
    }
}
