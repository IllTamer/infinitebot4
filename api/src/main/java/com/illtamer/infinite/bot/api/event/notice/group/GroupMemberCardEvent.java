package com.illtamer.infinite.bot.api.event.notice.group;

import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.annotation.Coordinates;
import com.illtamer.infinite.bot.api.event.notice.GroupNoticeEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 群成员名片更新事件
 * */
@Setter
@Getter
@ToString(callSuper = true)
@Coordinates(
        postType = Coordinates.PostType.NOTICE,
        secType = "group_card"
)
public class GroupMemberCardEvent extends GroupNoticeEvent {

    /**
     * 新名片
     * @apiNote 当名片为空时 card_xx 字段为空字符串, 并不是昵称
     * */
    @SerializedName("card_new")
    private String cardNew;

    /**
     * 旧名片
     * @apiNote 当名片为空时 card_xx 字段为空字符串, 并不是昵称
     * */
    @SerializedName("card_old")
    private String cardOld;

}
