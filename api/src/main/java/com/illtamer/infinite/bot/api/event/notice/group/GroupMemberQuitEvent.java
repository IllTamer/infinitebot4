package com.illtamer.infinite.bot.api.event.notice.group;

import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.annotation.Coordinates;
import com.illtamer.infinite.bot.api.event.notice.GroupNoticeEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 群成员减少事件
 * */
@Setter
@Getter
@ToString(callSuper = true)
@Coordinates(
        postType = Coordinates.PostType.NOTICE,
        secType = "group_decrease",
        subType = {"leave", "kick", "kick_me"}
)
public class GroupMemberQuitEvent extends GroupNoticeEvent {

    /**
     * 事件子类型, 分别表示主动退群、成员被踢、登录号被踢
     * <p>
     * leave、kick、kick_me
     * */
    @SerializedName("sub_type")
    private String subType;

    /**
     * 操作者 QQ 号 ( 如果是主动退群, 则和 user_id 相同 )
     * */
    @SerializedName("operator_id")
    private Long operatorId;

}
