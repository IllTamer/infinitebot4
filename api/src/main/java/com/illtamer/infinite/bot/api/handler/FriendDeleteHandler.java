package com.illtamer.infinite.bot.api.handler;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.Map;

/**
 * 删除好友
 * */
@Getter
public class FriendDeleteHandler extends AbstractAPIHandler<Map<String, Object>> {

    /**
     * 好友 QQ 号
     * */
    @SerializedName("friend_id")
    private Long friendId;

    public FriendDeleteHandler() {
        super("/delete_friend");
    }

    public FriendDeleteHandler setFriendId(Long friendId) {
        this.friendId = friendId;
        return this;
    }

}
