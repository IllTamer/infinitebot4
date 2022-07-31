package com.illtamer.infinite.bot.api.entity.enumerate;

import lombok.Getter;

/**
 * 音乐分享歌曲来源枚举类
 * */
public enum MusicType {

    QQ("qq"),

    _163("163"),

    XM("xm");

    @Getter
    private final String value;

    MusicType(String value) {
        this.value = value;
    }

}
