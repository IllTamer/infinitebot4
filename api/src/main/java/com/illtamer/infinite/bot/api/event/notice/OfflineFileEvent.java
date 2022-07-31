package com.illtamer.infinite.bot.api.event.notice;

import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.annotation.Coordinates;
import com.illtamer.infinite.bot.api.entity.File;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 接收离线文件事件
 * */
@Setter
@Getter
@ToString(callSuper = true)
@Coordinates(
        postType = Coordinates.PostType.NOTICE,
        secType = "offline_file"
)
public class OfflineFileEvent extends NoticeEvent {

    /**
     * 发送者id
     * */
    @SerializedName("user_id")
    private Long userId;

    /**
     * 文件数据
     * */
    private File file;

}
