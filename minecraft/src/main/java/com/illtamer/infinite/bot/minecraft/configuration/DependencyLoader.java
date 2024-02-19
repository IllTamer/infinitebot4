package com.illtamer.infinite.bot.minecraft.configuration;

import com.illtamer.infinite.bot.minecraft.api.BotScheduler;
import com.illtamer.infinite.bot.minecraft.api.adapter.Bootstrap;
import com.illtamer.infinite.bot.minecraft.util.ProcessBar;
import dev.vankka.dependencydownload.DependencyManager;
import dev.vankka.dependencydownload.repository.Repository;
import dev.vankka.dependencydownload.repository.StandardRepository;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * 依赖库动态加载器
 * */
public class DependencyLoader {

    public static final List<Repository> REPOSITORIES = Arrays.asList(
            new StandardRepository("https://repo.maven.apache.org/maven2"),
            new StandardRepository("https://oss.sonatype.org/content/repositories/snapshots"),
            new StandardRepository("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    );

    @SneakyThrows
    public static void load(Bootstrap instance) {
        File dependencyFolder = new File(instance.getDataFolder(), "libs");
        if (!dependencyFolder.exists()) dependencyFolder.mkdirs();
        ExecutorService executor = BotScheduler.IO_INTENSIVE;

        DependencyManager manager = new DependencyManager(dependencyFolder.toPath());
        String resource = new BufferedReader(new InputStreamReader(instance.getResource("runtimeDownload.txt")))
                .lines().collect(Collectors.joining(System.lineSeparator()));
        manager.loadFromResource(resource);
        System.out.println("getRelocations: " + manager.getRelocations().size());

        instance.getLogger().info("Download dependencies ...");
        CompletableFuture<Void>[] downloadFutures = manager.download(executor, REPOSITORIES);
        ProcessBar downloadBar = ProcessBar.create(downloadFutures.length);
        for (CompletableFuture<Void> future : downloadFutures) {
            executor.submit(() -> {
                future.join();
                downloadBar.count();
            });
        }

        manager.relocateAll(executor).join();
        instance.getLogger().info("Loading dependencies ...");
        CompletableFuture<Void>[] loadFutures = manager.load(executor, (path) -> {
            try {
                URLClassLoader classLoader = (URLClassLoader) instance.getInstClassLoader();
                Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                addURL.setAccessible(true);
                addURL.invoke(classLoader, path.toUri().toURL());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
//        ProcessBar loadBar = ProcessBar.create(loadFutures.length);
        for (CompletableFuture<Void> future : loadFutures) {
            executor.submit(() -> {
                future.join();
//                loadBar.count();
            });
        }
    }

}
