package com.illtamer.infinite.bot.api.event.notice.group;

import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.annotation.Coordinates;
import com.illtamer.infinite.bot.api.event.notice.GroupNoticeEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 群成员头衔变更事件
 * */
@Setter
@Getter
@ToString(callSuper = true)
@Coordinates(
        postType = Coordinates.PostType.NOTICE,
        secType = "notify",
        subType = {"title"}
)
public class GroupMemberTitleChangeEvent extends GroupNoticeEvent {

    /**
     * 事件子类型
     * <p>
     * title
     * */
    @SerializedName("sub_type")
    private String subType;

    /**
     * 获得的新头衔
     * */
    private String title;

}
