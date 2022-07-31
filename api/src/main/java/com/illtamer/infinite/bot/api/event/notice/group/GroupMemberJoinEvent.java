package com.illtamer.infinite.bot.api.event.notice.group;

import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.annotation.Coordinates;
import com.illtamer.infinite.bot.api.event.notice.GroupNoticeEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 群成员增加事件
 * */
@Setter
@Getter
@ToString(callSuper = true)
@Coordinates(
        postType = Coordinates.PostType.NOTICE,
        secType = "group_increase",
        subType = {"approve", "invite"}
)
public class GroupMemberJoinEvent extends GroupNoticeEvent {

    /**
     * 事件子类型, 分别表示管理员已同意入群、管理员邀请入群
     * <p>
     * approve、invite
     * */
    @SerializedName("sub_type")
    private String subType;

    /**
     * 操作者 QQ 号
     * */
    @SerializedName("operator_id")
    private Long operatorId;

}
