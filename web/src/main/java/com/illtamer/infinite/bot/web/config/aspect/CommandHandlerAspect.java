package com.illtamer.infinite.bot.web.config.aspect;

import com.illtamer.infinite.bot.api.event.message.MessageEvent;
import com.illtamer.infinite.bot.api.util.Assert;
import com.illtamer.infinite.bot.web.annotation.CommandHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CommandHandlerAspect {

    @Pointcut("@annotation(com.illtamer.infinite.bot.web.annotation.CommandHandler)")
    private void point() {}

    @Around("point()")
    protected Object doAround(ProceedingJoinPoint pjq) throws Throwable {
        Object[] args = pjq.getArgs();
        Assert.isTrue(args.length == 1 && args[0] instanceof MessageEvent, "Illegal commandHandler method");

        MethodSignature signature = (MethodSignature) pjq.getSignature();
        CommandHandler commandHandler = signature.getMethod().getAnnotation(CommandHandler.class);
        String prefix = commandHandler.prefix();

        String rawMessage = ((MessageEvent) args[0]).getRawMessage();
        if (!rawMessage.startsWith(prefix)) return null;
        String[] split = rawMessage.split(" ");
        if (commandHandler.length() != -1)
            if (!prefix.equals(split[0]) || split.length-1 < commandHandler.length()) return null;
        return pjq.proceed(args);
    }

}
