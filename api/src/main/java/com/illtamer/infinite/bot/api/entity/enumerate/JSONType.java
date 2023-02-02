package com.illtamer.infinite.bot.api.entity.enumerate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.illtamer.infinite.bot.api.util.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@AllArgsConstructor
public class JSONType {

    public static final int UNKNOWN = -1;

    /**
     * 音乐分享
     * */
    public static final int MUSIC_SHARE = 10001;

    /**
     * 群公告
     * */
    public static final int GROUP_ANNOUNCE = 10002;

    private static final JSONType UNKNOWN_JSONTYPE;

    @Getter
    private final String name;

    @Getter
    private final Map<String, String> content;

    @Getter
    private final int type;

    public static JSONType getJSONType(String json) {
        JsonObject object = new Gson().fromJson(json, JsonObject.class);
        switch (object.get("app").getAsString()) {
            case "com.tencent.mannounce":
                return parseAnnounce(object);
            case "com.tencent.structmsg":
                return parseStructMsg(object);
        }
        return UNKNOWN_JSONTYPE;
    }

    private static JSONType parseStructMsg(JsonObject object) {
        JsonObject wyyMeta = object.getAsJsonObject("meta.music");
        JsonObject qqMeta = object.getAsJsonObject("meta.news");
        if (wyyMeta == null && qqMeta == null) return UNKNOWN_JSONTYPE;
        JsonObject meta = wyyMeta == null ? qqMeta : wyyMeta;
        Map<String, String> data = Maps.of(
                // 来源 网易云音乐
                "tag", meta.get("tag").getAsString(),
                // 歌曲名称
                "title", meta.get("title").getAsString(),
                // 作者
                "author", meta.get("desc").getAsString(),
                // 封面url
                "preview", meta.get("preview").getAsString(),
                // 跳转地址
                "jumpUrl", meta.get("jumpUrl").getAsString()
        );
        return new JSONType("MUSIC_SHARE", data, MUSIC_SHARE);
    }

    private static JSONType parseAnnounce(JsonObject object) {
        JsonObject meta = object.getAsJsonObject("meta.mannounce");
        if (meta == null) return UNKNOWN_JSONTYPE;
        Map<String, String> data = Maps.of(
                // 公告内容
                "content", object.get("prompt").getAsString()
        );
        return new JSONType("GROUP_ANNOUNCE", data, GROUP_ANNOUNCE);
    }

    static {
        UNKNOWN_JSONTYPE = new JSONType("UNKNOWN", Collections.emptyMap(), UNKNOWN);
    }

}
