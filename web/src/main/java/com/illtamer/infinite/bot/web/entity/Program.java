package com.illtamer.infinite.bot.web.entity;


import com.illtamer.infinite.bot.api.event.message.MessageEvent;
import com.illtamer.infinite.bot.web.config.ThreadPoolConfiguration;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Getter
public class Program {

    /**
     * 程序名称
     * */
    private final String name;

    private final Process process;

    private final BufferedReader reader;

    private final BufferedWriter writer;

    /**
     * @param path 程序路径
     * @param jarName Jar 文件名
     * */
    @SneakyThrows(IOException.class)
    public Program(String name, String path, String jarName) {
        this.name = name;
        this.process = new ProcessBuilder()
                .directory(new File(path))
                .command("java", "-jar", jarName)
                .redirectErrorStream(true)
                .start();
        this.reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
        this.writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), "GBK"));
    }

    @SneakyThrows(IOException.class)
    public void holdResponse(MessageEvent event, long delay) {
        String line;
        ScheduledFuture<?> future = null;
        StringBuffer builder = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            if (future == null || future.isDone()) {
                future = ThreadPoolConfiguration.DELAY_POOL.schedule(() -> {
                    event.reply(builder.toString());
                    builder.setLength(0);
                }, delay, TimeUnit.MILLISECONDS);
            }
            builder.append(line).append('\n');
        }
        event.reply("![Tip]: Process " + name + " closed !");
    }

    @SneakyThrows(IOException.class)
    public void sendParam(String param) {
        writer.write(param);
        writer.newLine();
        writer.flush();
    }

    @SneakyThrows(IOException.class)
    public void shutdown() {
        writer.close();
        process.destroyForcibly();
        reader.close();
    }
}
