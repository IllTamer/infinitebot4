package com.illtamer.infinite.bot.api.entity;

import lombok.Data;

/**
 * 匿名用户信息
 * */
@Data
public class Anonymous {

    /**
     * 匿名用户 ID
     * */
    private Long id;

    /**
     * 匿名用户名称
     * */
    private String name;

    /**
     * 匿名用户 flag, 在调用禁言 API 时需要传入
     * */
    private String flag;

}
