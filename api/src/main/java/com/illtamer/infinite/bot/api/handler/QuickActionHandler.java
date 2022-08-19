package com.illtamer.infinite.bot.api.handler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.illtamer.infinite.bot.api.event.Event;
import com.illtamer.infinite.bot.api.message.Message;
import lombok.Getter;

import java.util.Map;

/**
 * 快速操作 APIHandler
 * TODO deprecated
 * */
@Getter
@Deprecated
public class QuickActionHandler extends AbstractAPIHandler<Map<String, Object>> {

    /**
     * 事件数据对象
     * <p>
     * 可做精简, 如去掉 message 等无用字段
     * */
    private final Event context;

    /**
     * 快速操作对象
     * <p>
     * 例如 {"ban": true, "reply": "请不要说脏话"}
     * */
    private final JsonObject operation = new JsonObject();

    public QuickActionHandler(Event context) {
        super("/.handle_quick_operation");
        this.context = context;
    }

    public QuickActionHandler addOperation(String key, String value) {
        this.operation.addProperty(key, value);
        return this;
    }

    public QuickActionHandler addOperation(String key, Boolean value) {
        this.operation.addProperty(key, value);
        return this;
    }

    public QuickActionHandler addOperation(String key, Number value) {
        this.operation.addProperty(key, value);
        return this;
    }

    public QuickActionHandler addOperation(String key, Message value) {
        this.operation.add(key, new Gson().fromJson(value.toString(), JsonArray.class));
        return this;
    }

}
