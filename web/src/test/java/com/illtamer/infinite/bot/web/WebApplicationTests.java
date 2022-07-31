package com.illtamer.infinite.bot.web;

import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.Scanner;

@SpringBootTest
class WebApplicationTests {

    // 测试自定义合并消息
    public static void main(String[] args) throws IOException, InterruptedException {
//        final ExecutorService service = Executors.newCachedThreadPool();
        String command = "casual-1.0.0-SNAPSHOT-jar-with-dependencies.jar";
        Process process = new ProcessBuilder()
                .directory(new File("E:\\JCode\\idea\\gitee\\casual"))
                .command("java", "-jar", command)
                .redirectErrorStream(true)
                .start();
        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
                String line;
                while ((line = reader.readLine()) != null)
                    System.out.println(line);
                System.out.println("输出终止");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), "GBK"));
                Scanner scanner = new Scanner(System.in);
                while (scanner.hasNextLine()) {
                    writer.write(scanner.nextLine());
                    writer.newLine();
                    writer.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        process.waitFor();
    }

}
