package com.illtamer.infinite.bot.api.event.notice.group;

import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.annotation.Coordinates;
import com.illtamer.infinite.bot.api.event.notice.GroupNoticeEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 群红包运气王提示事件
 * */
@Setter
@Getter
@ToString(callSuper = true)
@Coordinates(
        postType = Coordinates.PostType.NOTICE,
        secType = "notify",
        subType = "lucky_king"
)
public class GroupRedEnvelopeLuckiestEvent extends GroupNoticeEvent {

    /**
     * 提示类型
     * <p>
     * lucky_king
     * */
    @SerializedName("sub_type")
    private String subType;

    /**
     * 运气王id
     * */
    @SerializedName("target_id")
    private Long targetId;

}
