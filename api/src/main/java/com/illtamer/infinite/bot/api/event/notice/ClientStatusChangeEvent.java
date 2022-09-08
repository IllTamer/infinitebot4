package com.illtamer.infinite.bot.api.event.notice;

import com.illtamer.infinite.bot.api.annotation.Coordinates;
import com.illtamer.infinite.bot.api.entity.Device;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * 其他客户端在线状态变更事件
 * */
@Setter
@Getter
@ToString(callSuper = true)
@Coordinates(
        postType = Coordinates.PostType.NOTICE,
        secType = "client_status"
)
public class ClientStatusChangeEvent extends NoticeEvent {

    /**
     * 客户端信息
     * */
    private Device client;

    /**
     * 当前是否在线
     * */
    private Boolean online;

    /**
     * @apiNote null
     * */
    private Date time = null;

}
