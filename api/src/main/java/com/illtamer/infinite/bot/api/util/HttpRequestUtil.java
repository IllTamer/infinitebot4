package com.illtamer.infinite.bot.api.util;

import com.google.gson.Gson;
import com.illtamer.infinite.bot.api.Pair;
import com.illtamer.infinite.bot.api.handler.APIHandler;
import lombok.SneakyThrows;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

public class HttpRequestUtil {

    @SneakyThrows(IOException.class)
    public static String postJson(String url, Object payload, @Nullable Map<String, String> headers) {
        final String payloadJson = parsePayload(payload);
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(3000);
        client.getHttpConnectionManager().getParams().setSoTimeout(3000);
        client.getParams().setContentCharset("UTF-8");
        PostMethod postMethod = new PostMethod(url);
        postMethod.setRequestHeader("Content-Type", "application/json");
        if (headers != null && headers.size() != 0)
            headers.entrySet().stream()
                    .filter(entry -> entry.getValue() != null)
                    .forEach(entry -> postMethod.setRequestHeader(entry.getKey(), entry.getValue()));

        StringRequestEntity requestEntity = new StringRequestEntity(payloadJson, "application/json", "UTF-8");
        postMethod.setRequestEntity(requestEntity);
        int status = client.executeMethod(postMethod);
        if (status == HttpStatus.SC_OK)
            return postMethod.getResponseBodyAsString();
        throw new RuntimeException("接口连接失败！");
    }

    @SneakyThrows(IOException.class)
    public static Pair<Integer, String> getJson(String url, @Nullable Map<String, String> params) {
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(3000);
        client.getHttpConnectionManager().getParams().setSoTimeout(3000);
        client.getParams().setContentCharset("UTF-8");
        GetMethod getMethod = new GetMethod(concatUrl(url, params));
        int status = client.executeMethod(getMethod);
        return new Pair<>(status, getMethod.getResponseBodyAsString());
    }

    @NotNull
    public static String parsePayload(@Nullable Object payload) {
        String content = "";
        if (payload == null) return content;
        try {
            content = new Gson().toJson(payload);
        } catch (Exception e) {
            System.err.printf("Some exception occurred when parse payload with endpoint: %s, %s",
                    ((APIHandler<?>) payload).getEndpoint(), e);
        }
        return content;
    }

    @NotNull
    private static String concatUrl(@NotNull String url, @Nullable Map<String, String> params) {
        if (params == null || params.size() == 0) return url;
        StringBuilder builder = new StringBuilder(url).append('?');
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
        }
        return builder.deleteCharAt(builder.length()-1).toString();
    }

}
