package com.illtamer.infinite.bot.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

// 暴露代理
// 代理方法应使用 (AopContext.currentProxy()).method() 形式调用
@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

}
