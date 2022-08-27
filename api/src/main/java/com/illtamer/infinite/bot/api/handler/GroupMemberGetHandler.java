package com.illtamer.infinite.bot.api.handler;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.Response;
import com.illtamer.infinite.bot.api.entity.GroupMember;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * 获取群成员信息
 * */
@Getter
public class GroupMemberGetHandler extends AbstractAPIHandler<Map<String, Object>> {

    @SerializedName("group_id")
    private Long groupId;

    @SerializedName("user_id")
    private Long userId;

    /**
     * 是否不使用缓存
     * <p>
     * （使用缓存可能更新不及时, 但响应更快）
     * */
    @SerializedName("no_cache")
    private Boolean noCache;

    public GroupMemberGetHandler() {
        super("/get_group_member_info");
    }

    public GroupMemberGetHandler setGroupId(Long groupId) {
        this.groupId = groupId;
        return this;
    }

    public GroupMemberGetHandler setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public GroupMemberGetHandler setNoCache(Boolean noCache) {
        this.noCache = noCache;
        return this;
    }

    @NotNull
    public static GroupMember parse(@NotNull Response<Map<String, Object>> response) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(response.getData()), GroupMember.class);
    }

}
