package com.illtamer.infinite.bot.api.event.notice;

import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.annotation.Coordinates;
import com.illtamer.infinite.bot.api.event.Event;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 通知上报事件
 * */
@Setter
@Getter
@ToString(callSuper = true)
@Coordinates(
        postType = Coordinates.PostType.NOTICE,
        secType = "*"
)
public class NoticeEvent extends Event {

    /**
     * 通知类型
     * <p>
     * group_upload
     * */
    @SerializedName("notice_type")
    private String noticeType;

}
