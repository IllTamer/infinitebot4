package com.illtamer.infinite.bot.api.handler;

import com.google.gson.Gson;
import com.illtamer.infinite.bot.api.Response;
import lombok.Data;
import lombok.Getter;

import java.util.Map;

/**
 * 获取图片信息
 * */
@Getter
public class ImageGetHandler extends AbstractAPIHandler<Map<String, Object>> {

    private String file;

    public ImageGetHandler() {
        super("/get_image");
    }

    public ImageGetHandler setFile(String file) {
        this.file = file;
        return this;
    }

    public static Image parse(Response<Map<String, Object>> response) {
        final Gson gson = new Gson();
        return gson.fromJson(gson.toJson(response.getData()), Image.class);
    }

    @Data
    public static class Image {

        /**
         * 图片源文件大小
         * */
        private Integer size;

        /**
         * 图片文件原名
         * */
        private String filename;

        /**
         * 图片下载地址
         * */
        private String url;

    }

}
