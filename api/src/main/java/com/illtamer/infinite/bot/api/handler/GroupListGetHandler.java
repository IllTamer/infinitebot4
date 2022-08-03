package com.illtamer.infinite.bot.api.handler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.illtamer.infinite.bot.api.Response;
import com.illtamer.infinite.bot.api.entity.Group;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取群列表
 * */
public class GroupListGetHandler extends AbstractAPIHandler<List<?>> {

    @Getter
    @Nullable
    private static List<Group> cacheGroups;

    public GroupListGetHandler() {
        super("/get_group_list");
    }

    @NotNull
    public static List<Group> parse(@NotNull Response<List<?>> response) {
        final Gson gson = new Gson();
        final JsonArray array = gson.fromJson(gson.toJson(response.getData()), JsonArray.class);
        List<Group> groups = new ArrayList<>(array.size());
        array.forEach(object -> groups.add(gson.fromJson(object, Group.class)));
        return cacheGroups = groups;
    }

}
