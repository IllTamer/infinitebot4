package com.illtamer.infinite.bot.api.util;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

public class HttpRequestUtil {

    @SneakyThrows(IOException.class)
    public static String postJson(String url, Object payload, @Nullable Map<String, String> headers) {
        final String json = new Gson().toJson(payload);

        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(3000);
        client.getHttpConnectionManager().getParams().setSoTimeout(3*60*1000);
        client.getParams().setContentCharset("UTF-8");
        PostMethod postMethod = new PostMethod(url);
        postMethod.setRequestHeader("Content-Type", "application/json");
        if (headers != null && headers.size() != 0)
            headers.entrySet().stream()
                    .filter(entry -> entry.getValue() != null)
                    .forEach(entry -> postMethod.setRequestHeader(entry.getKey(), entry.getValue()));

        StringRequestEntity requestEntity = new StringRequestEntity(json, "application/json", "UTF-8");
        postMethod.setRequestEntity(requestEntity);
        int status = client.executeMethod(postMethod);
        if (status == HttpStatus.SC_OK)
            return postMethod.getResponseBodyAsString();
        throw new RuntimeException("接口连接失败！");
    }

}
