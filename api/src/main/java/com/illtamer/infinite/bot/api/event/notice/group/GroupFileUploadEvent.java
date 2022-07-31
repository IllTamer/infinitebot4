package com.illtamer.infinite.bot.api.event.notice.group;

import com.illtamer.infinite.bot.api.annotation.Coordinates;
import com.illtamer.infinite.bot.api.entity.File;
import com.illtamer.infinite.bot.api.event.notice.GroupNoticeEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 群文件上传事件
 * */
@Setter
@Getter
@ToString(callSuper = true)
@Coordinates(
        postType = Coordinates.PostType.NOTICE,
        secType = "group_upload"
)
public class GroupFileUploadEvent extends GroupNoticeEvent {

    /**
     * 文件信息
     * */
    private File file;

}
