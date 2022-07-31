package com.illtamer.infinite.bot.api.event.notice.group;

import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.annotation.Coordinates;
import com.illtamer.infinite.bot.api.event.notice.GroupNoticeEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 群消息撤回事件
 * */
@Setter
@Getter
@ToString(callSuper = true)
@Coordinates(
        postType = Coordinates.PostType.NOTICE,
        secType = "group_recall"
)
public class GroupMessageRecallEvent extends GroupNoticeEvent {

    /**
     * 操作者 QQ 号
     * */
    @SerializedName("operator_id")
    private Long operatorId;

    /**
     * 被撤回的消息 ID
     * */
    @SerializedName("message_id")
    private Long messageId;

}
