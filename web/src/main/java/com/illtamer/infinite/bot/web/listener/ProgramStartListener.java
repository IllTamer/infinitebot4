package com.illtamer.infinite.bot.web.listener;

import com.illtamer.infinite.bot.api.event.message.MessageEvent;
import com.illtamer.infinite.bot.api.util.Maps;
import com.illtamer.infinite.bot.web.annotation.CommandHandler;
import com.illtamer.infinite.bot.web.entity.Program;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProgramStartListener {

    // programName, fileName
    private static final Map<String, String> PROGRAM_FILE_NAME_MAP = Maps.of("casual", "casual-1.0.0-SNAPSHOT-jar-with-dependencies.jar");

    private static final Map<String, String> PROGRAM_PATH_MAP = Maps.of("casual", "E:\\JCode\\idea\\gitee\\casual");

    // userId, program
    private final Map<Long, Program> programMap = new HashMap<>();

    /**
     * 每个权限者同时仅能存在一个程序
     * */
    @EventListener
    @CommandHandler(prefix = "/run", length = 1)
    public void onRunProgram(MessageEvent event) {
        // Authentication
        Long userId = event.getUserId();
        if (userId != 765743073) return;

        Program program = programMap.get(userId);
        String[] args = event.getRawMessage().split(" ");

        if (program == null) {
            ((ProgramStartListener) AopContext.currentProxy()).createProgram(args, event);
        } else {
            if ("stop".equals(args[1])) {
                programMap.remove(userId).shutdown();
                return;
            }
            if (!"input".equals(args[1]) && args.length < 3) {
                event.reply("请按以下格式输入参数: /run input [param...]");
                return;
            }
            program.sendParam(event.getRawMessage().substring("/run input ".length()));
        }
    }

    @Async("programThreadPool")
    protected void createProgram(String[] args, MessageEvent event) {
        if (!PROGRAM_FILE_NAME_MAP.containsKey(args[1])) {
            event.reply("请按以下格式输入指令: /run " + PROGRAM_FILE_NAME_MAP.keySet());
        } else {
            Program create = new Program(args[1], PROGRAM_PATH_MAP.get(args[1]), PROGRAM_FILE_NAME_MAP.get(args[1]));
            programMap.put(event.getUserId(), create);
            create.holdResponse(event, 300L);
            programMap.remove(event.getUserId());
        }
    }

}
