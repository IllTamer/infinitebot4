package com.illtamer.infinite.bot.minecraft;

import com.illtamer.infinite.bot.minecraft.configuration.config.CommentConfiguration;

public class BootstrapTests {


//    public static void main(String[] args) {
////        CQHttpWebSocketConfiguration.setHttpUri("http://47.117.136.149:5700");
////        CQHttpWebSocketConfiguration.setAuthorization("root765743073");
////        System.out.println(OpenAPIHandling.getMessage(1051692076));
//
//    }

    public static void main(String[] args) throws Exception {
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
        CommentConfiguration config = new CommentConfiguration();
        config.loadFromString("point1: '123456'\n" +
                "part:\n" +
                "    #666666\n" +
                "    point2: '#123456'");
        System.out.println(config.saveToString());
    }


}
