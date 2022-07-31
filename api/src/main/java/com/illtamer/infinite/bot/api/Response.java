package com.illtamer.infinite.bot.api;

import lombok.Data;

import java.util.Map;

@Data
public class Response {

    /**
     * 状态, 表示 API 是否调用成功
     * - ok	    api 调用成功
     * - async  api 调用已经提交异步处理, 具体 api 调用是否成功无法得知
     * - failed api 调用失败
     * */
    private final String status;

    /**
     * 响应码
     * - 0   调用成功
     * - 1   已提交 async 处理
     * - 其他 操作失败, 具体原因可以看响应的 msg 和 wording 字段
     * */
    private final Integer retcode;

    /**
     * 错误消息, 仅在 API 调用失败时存在该字段
     * */
    private final String msg;

    /**
     * 对错误的详细解释(中文), 仅在 API 调用失败时有该字段
     * */
    private final String wording;

    /**
     * 响应数
     * - key: 响应数据名
     * - value: 数据值
     * */
    private final Map<String, Object> data;

    /**
     * '回声', 如果请求时指定了 echo, 那么响应也会包含 echo
     * */
    private final String echo;
}
