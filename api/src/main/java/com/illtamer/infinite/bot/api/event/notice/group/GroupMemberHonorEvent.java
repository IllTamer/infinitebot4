package com.illtamer.infinite.bot.api.event.notice.group;

import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.annotation.Coordinates;
import com.illtamer.infinite.bot.api.event.notice.GroupNoticeEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 群成员荣誉变更提示事件
 * */
@Setter
@Getter
@ToString(callSuper = true)
@Coordinates(
        postType = Coordinates.PostType.NOTICE,
        secType = "notify",
        subType = "honor"
)
public class GroupMemberHonorEvent extends GroupNoticeEvent {

    /**
     * 提示类型
     * <p>
     * honor
     * */
    @SerializedName("sub_type")
    private String subType;

    /**
     * 荣誉类型
     * <p>
     * talkative:龙王 performer:群聊之火 emotion:快乐源泉
     * */
    @SerializedName("honor_type")
    private String honorType;

}
