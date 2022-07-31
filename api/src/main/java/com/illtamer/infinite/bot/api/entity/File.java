package com.illtamer.infinite.bot.api.entity;

import lombok.Data;

/**
 * 文件信息
 * */
@Data
public class File {

    /**
     * 文件 ID
     * */
    private String id;

    /**
     * 文件名
     * */
    private String name;

    /**
     * 文件大小 ( 字节数 )
     * */
    private Long size;

    /**
     * busid ( 目前不清楚有什么作用 )
     * */
    private Long busid;

}
