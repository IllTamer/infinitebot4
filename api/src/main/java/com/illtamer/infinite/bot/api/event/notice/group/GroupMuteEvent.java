package com.illtamer.infinite.bot.api.event.notice.group;

import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.annotation.Coordinates;
import com.illtamer.infinite.bot.api.event.notice.GroupNoticeEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 群禁言事件
 * */
@Setter
@Getter
@ToString(callSuper = true)
@Coordinates(
        postType = Coordinates.PostType.NOTICE,
        secType = "group_ban",
        subType = {"ban", "lift_ban"}
)
public class GroupMuteEvent extends GroupNoticeEvent {

    /**
     * 事件子类型, 分别表示禁言、解除禁言
     * <p>
     * ban、lift_ban
     * */
    @SerializedName("sub_type")
    private String subType;

    /**
     * 操作者 QQ 号
     * */
    @SerializedName("operator_id")
    private Long operatorId;

    /**
     * 禁言时长, 单位秒
     * */
    private Long duration;

}
