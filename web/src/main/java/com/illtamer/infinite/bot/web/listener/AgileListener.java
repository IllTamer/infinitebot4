package com.illtamer.infinite.bot.web.listener;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.illtamer.infinite.bot.api.event.message.MessageEvent;
import com.illtamer.infinite.bot.api.message.Message;
import com.illtamer.infinite.bot.api.message.MessageBuilder;
import com.illtamer.infinite.bot.api.util.HttpRequestUtil;
import com.illtamer.infinite.bot.web.annotation.CommandHandler;
import com.illtamer.infinite.bot.web.annotation.Permission;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 敏捷监听
 * */
@Component
public class AgileListener {

    private static final JsonObject OBJECT = new JsonObject();

    static {
        OBJECT.addProperty("r18", 1);
        OBJECT.addProperty("num", 1);
        JsonArray size = new JsonArray();
        size.add("regular");
        OBJECT.add("size", size);
    }

    /**
     * @apiNote https://api.lolicon.app/#/setu
     * */
    @Permission // TODO
    @CommandHandler(prefix = "/色图")
    @EventListener
    public void onR18(MessageEvent event) {
        if (event.getUserId() != 765743073) return;
        JsonObject object = OBJECT.deepCopy();
        String[] keys = event.getRawMessage().split(" ");
        if (keys.length != 1) {
            JsonArray tag = new JsonArray();
            for (int i = 1; i < keys.length; ++ i) tag.add(keys[i]);
            object.add("tag", tag);
        }
        String json = HttpRequestUtil.postJson("https://api.lolicon.app/setu/v2", object, null);
        final JsonObject response = new Gson().fromJson(json, JsonObject.class);
        String error = response.get("error").getAsString();
        if (error != null && error.length() != 0)
            event.reply(error);
        else {
            JsonObject dataFirst = (JsonObject) response.get("data").getAsJsonArray().get(0);
            final Message message = MessageBuilder.json()
                    .text("标题: " + dataFirst.get("title").getAsString())
                    .text("作者: " + dataFirst.get("author").getAsString())
                    .text("标签: " + dataFirst.getAsJsonArray("tags").toString())
                    .image(dataFirst.get("title").getAsString(), dataFirst.getAsJsonObject("urls").get("regular").getAsString())
                    .build();
            event.reply(message);
        }
    }

}
