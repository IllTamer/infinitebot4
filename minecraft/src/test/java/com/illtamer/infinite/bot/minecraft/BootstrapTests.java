package com.illtamer.infinite.bot.minecraft;

import com.illtamer.infinite.bot.minecraft.api.BotScheduler;
import com.illtamer.infinite.bot.minecraft.configuration.config.CommentConfiguration;
import com.illtamer.infinite.bot.minecraft.util.ProcessBar;
import dev.vankka.dependencydownload.DependencyManager;
import dev.vankka.dependencydownload.classloader.IsolatedClassLoader;
import dev.vankka.dependencydownload.repository.Repository;
import dev.vankka.dependencydownload.repository.StandardRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class BootstrapTests {


//    public static void main(String[] args) {
////        CQHttpWebSocketConfiguration.setHttpUri("http://47.117.136.149:5700");
////        CQHttpWebSocketConfiguration.setAuthorization("root765743073");
////        System.out.println(OpenAPIHandling.getMessage(1051692076));
//
//    }

    public static void main(String[] args) throws Exception {
        processBar();
//        testLibsLoader();
        System.out.println("hhh");
//        DependencyManager manager = new DependencyManager(new File("cache").toPath());
//        manager.addDependency(new StandardDependency(
//                "com.illtamer.infinite.bot",
//                "api",
//                "1.0.13",
//                null,
//                "081904f730f0a03d5959bc764b8d196b4d96a041cd66d026b40ed3ae41d5721e",
//                "SHA-256"
//        ));
//        Executor executor = Executors.newCachedThreadPool();

        // All of these return CompletableFuture it is important to let the previous step finishing before starting the next
//        manager.downloadAll(executor, Collections.singletonList(new StandardRepository("https://repo.maven.apache.org/maven2"))).join();
//        manager.loadAll(executor, new IsolatedClassLoader()).join();
    }

    private static void testCommentConfig() throws Exception {
//        CommentConfiguration config = new CommentConfiguration();
//        config.loadFromString("point1: '123456'\n" +
//                "part:\n" +
//                "    #666666\n" +
//                "    point2: '#123456'");
//        System.out.println(config.saveToString());
    }

    private static void testLibsLoader() throws Exception {
        List<Repository> REPOSITORIES = Arrays.asList(
                new StandardRepository("https://repo.maven.apache.org/maven2"),
                new StandardRepository("https://oss.sonatype.org/content/repositories/snapshots"),
                new StandardRepository("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        );

        File dependencyFolder = new File("/home/illtamer/Code/java/idea/github/infinitebot4/minecraft/src/test/resources/libs");
        if (!dependencyFolder.exists()) dependencyFolder.mkdirs();
        ExecutorService executor = Executors.newFixedThreadPool(32);

        DependencyManager manager = new DependencyManager(dependencyFolder.toPath());
        String resource = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get("/home/illtamer/Code/java/idea/github/infinitebot4/minecraft/src/test/resources/runtimeDownload.txt"))))
                .lines().collect(Collectors.joining(System.lineSeparator()));

        manager.loadFromResource(resource);
        System.out.println("start download");
        manager.downloadAll(executor, REPOSITORIES).join();
        System.out.println("download done");
        // manager.relocateAll(executor).join();

        CompletableFuture<Void>[] tasks = manager.load(executor, new IsolatedClassLoader());
        for (int i = 0; i < tasks.length; i++) {
            tasks[i].join();
            System.out.println(String.format("[Done] %d/%d", i+1, tasks.length));
        }
        System.out.println("Finish");
    }

    private static void processBar() throws Exception {
        int total = 10;
        ProcessBar b = ProcessBar.create(total);
        for (int i = 0; i < total; i++) {
            b.count();
            Thread.sleep(1000);
        }
    }

}
