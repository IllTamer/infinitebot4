package com.illtamer.infinite.bot.api.exception;

import com.illtamer.infinite.bot.api.Response;

/**
 * API 调用异常
 * */
public class APIInvokeException extends RuntimeException {

    public APIInvokeException() {
    }

    public APIInvokeException(Response response) {
        super(response.getMsg());
    }

    public APIInvokeException(Response response, Throwable cause) {
        super(response.getMsg(), cause);
    }

}
