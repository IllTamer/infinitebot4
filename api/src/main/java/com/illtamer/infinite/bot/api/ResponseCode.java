package com.illtamer.infinite.bot.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    NO_ACCESS_TOKEN("access token 未提供", 401),

    ERROR_ACCESS_TOKEN("access token 不符合", 403),

    UNSUPPORTED_CONTENT_TYPE("Content-Type 不支持 (非 application/json 或 application/x-www-form-urlencoded", 406),

    UNKNOWN_API("API 不存在", 404),

    SUCCESS("除上述情况外所有情况 (具体 API 调用是否成功, 需要看 API 的 响应数据", 200);

    private final String message;

    private final int code;

}
