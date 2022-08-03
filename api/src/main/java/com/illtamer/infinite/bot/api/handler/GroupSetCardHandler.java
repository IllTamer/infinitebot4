package com.illtamer.infinite.bot.api.handler;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.Map;

@Getter
public class GroupSetCardHandler extends AbstractAPIHandler<Map<String, Object>> {

    /**
     * 群号
     * */
    @SerializedName("group_id")
    private Long groupId;

    /**
     * 要设置的 QQ 号
     * */
    @SerializedName("user_id")
    private Long userId;

    /**
     * 群名片内容, 不填或空字符串表示删除群名片
     * */
    private String card;

    public GroupSetCardHandler() {
        super("/set_group_card");
    }

    public GroupSetCardHandler setGroupId(Long groupId) {
        this.groupId = groupId;
        return this;
    }

    public GroupSetCardHandler setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public GroupSetCardHandler setCard(String card) {
        this.card = card;
        return this;
    }

}
