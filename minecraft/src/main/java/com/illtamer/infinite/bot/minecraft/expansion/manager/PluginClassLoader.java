package com.illtamer.infinite.bot.minecraft.expansion.manager;


import com.illtamer.infinite.bot.api.util.Assert;
import org.bukkit.plugin.InvalidPluginException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * 用于载入一个jar内的所有class
 */
public class PluginClassLoader extends URLClassLoader {
    private final Map<String, Class<?>> classes = new ConcurrentHashMap<>(10);
    private final InfinitePluginLoader loader;
    private final JarFile jar;
    private final File jarFile;
    private final URL url;
    private final Manifest manifest;
    private final String folderName;
    final InfiniteExpansion expansion;
    // 仅用于判断附属是否已有实例
    private InfiniteExpansion expansionInit;
    private IllegalStateException expansionState;

    static { // 支持并行加载
        ClassLoader.registerAsParallelCapable();
    }

    public PluginClassLoader(InfinitePluginLoader loader, ClassLoader parent, File jarFile) throws IOException, InvalidPluginException {
        super(new URL[] {jarFile.toURI().toURL()}, parent);
        this.loader = loader;
        this.jarFile = jarFile;
        this.jar = new JarFile(jarFile);
        this.url = jarFile.toURI().toURL();
        this.manifest = jar.getManifest();
        try {
            Class<?> mainClass = getExpansionClass(jar, this);
            Class<? extends InfiniteExpansion> expansionClass;
            if (mainClass == null) {
                throw new InvalidPluginException("Cannot find main class");
            }
            this.folderName = mainClass.getSimpleName();
            try {
                expansionClass = mainClass.asSubclass(InfiniteExpansion.class);
            } catch (ClassCastException ex) {
                throw new InvalidPluginException("main class `" + mainClass.getSimpleName() + "' does not extend InfiniteExpansion", ex);
            }

            this.expansion = expansionClass.newInstance();
        } catch (IllegalAccessException ex) {
            throw new InvalidPluginException("No public constructor", ex);
        } catch (InstantiationException ex) {
            throw new InvalidPluginException("Abnormal plugin type", ex);
        } catch (Error | Exception ex) {
            throw new InvalidPluginException(String.format("When enabling jar '%s' there got an Unknown Exception !", jar.getName()), ex);
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
        if (name.startsWith("me.illtamer.infinitebot")) {
            throw new ClassNotFoundException(name);
        }
        Class<?> result = this.classes.get(name);
        if (result == null && checkGlobal) { // 检查所有同级loader
            result = this.loader.getGlobalClassByName(name);
        }
        if (result == null) { // 导包
            JarEntry entry = this.jar.getJarEntry(name.replace('.', '/') + ".class");
            if (entry != null) {
                byte[] bytes = getClassBytes(this.jar, entry);
                // 未知作用
                int dot = name.lastIndexOf('.');
                if (dot != -1) {
                    String pkgName = name.substring(0, dot);
                    if (getPackage(pkgName) == null) {
                        try {
                            if (this.manifest != null) {
                                definePackage(pkgName, this.manifest, this.url);
                            } else {
                                definePackage(pkgName, null, null, null, null, null, null, null);
                            }
                        } catch (IllegalArgumentException ex) {
                            if (getPackage(pkgName) == null) {
                                throw new IllegalStateException("Cannot find package " + pkgName);
                            }
                        }
                    }
                }
                result = defineClass(name, bytes, 0, bytes.length);
            }
            if (result == null) { // 委派
                result = super.findClass(name);
            }
            if (result != null) {
                this.loader.setGlobalClasses(name, result);
            }
            this.classes.put(name, result);
        }
        return result;
    }


    public void close() throws IOException {
        try {
            classes.clear();
            super.close();
        } finally {
            this.jar.close();
            System.gc();
        }
    }

    /**
     * 附属主类构建实例时调用
     * @see Class#newInstance() {@link InfiniteExpansion()}
     * */
    synchronized void initialize(InfiniteExpansion infiniteExpansion) {
        Assert.notNull(infiniteExpansion, "Initializing expansion cannot be null");
        Assert.isTrue((infiniteExpansion.getClass().getClassLoader() == this), "Cannot initialize expansion outside of this class loader");
        if (this.expansion != null || this.expansionInit != null) {
            throw new IllegalArgumentException("Expansion already initialized!", this.expansionState);
        }

        this.expansionState = new IllegalStateException("Initial initialization");
        this.expansionInit = infiniteExpansion;

        infiniteExpansion.init(this.loader, this.jarFile, this, folderName);
    }

    private static byte[] getClassBytes(JarFile jar, JarEntry entry) {
        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                InputStream input = jar.getInputStream(entry);
        ) {
            int line; byte[] buffer = new byte[1024];
            while ((line = input.read(buffer)) != -1) {
                out.write(buffer, 0, line);
            }
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    private static Class<?> getExpansionClass(JarFile jar, ClassLoader loader) {
        Enumeration<JarEntry> enumeration = jar.entries();
        int index;
        while (enumeration.hasMoreElements()) {
            JarEntry entry = enumeration.nextElement();
            if (!entry.isDirectory() && (index = entry.getName().indexOf(".class")) != -1) {
                try {
                    Class<?> clazz = Class.forName(entry.getName().substring(0, index).replace('/', '.'), false, loader);
                    if (InfiniteExpansion.class.isAssignableFrom(clazz) || (clazz.getSuperclass() != null && "me.illtamer.me.illtamer.infinitebot.expansion.manager.InfiniteExpansion".equals(clazz.getSuperclass().getName()))) {
                        return Class.forName(entry.getName().substring(0, index).replace('/', '.'), true, loader);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    protected Set<String> getClasses() {
        return classes.keySet();
    }

    public URL getResource(String name) { return findResource(name); }

    public Enumeration<URL> getResources(String name) throws IOException { return findResources(name); }
}
