package com.illtamer.infinite.bot.api.handler;

import com.google.gson.Gson;
import com.illtamer.infinite.bot.api.Response;
import com.illtamer.infinite.bot.api.config.CQHttpWebSocketConfiguration;
import com.illtamer.infinite.bot.api.exception.APIInvokeException;
import com.illtamer.infinite.bot.api.util.HttpRequestUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * API 逻辑接口
 * @apiNote https://docs.go-cqhttp.org/api/#
 * */
public interface APIHandler {

    Map<String, String> HEADERS = new HashMap<>(Collections.singletonMap("Authorization", CQHttpWebSocketConfiguration.getAuthorization()));

    /**
     * 获取终结点坐标
     * */
    String getEndpoint();

    /**
     * @throws APIInvokeException API 调用失败异常
     * */
    default Response request() {
        String json = HttpRequestUtil.postJson(CQHttpWebSocketConfiguration.getHttpUri() + getEndpoint(), this, HEADERS);
        Response response = new Gson().fromJson(json, Response.class);
        if ("failed".equals(response.getStatus()))
            throw new APIInvokeException(response);
        return response;
    }

}
