package com.illtamer.infinite.bot.api.event.request;

import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.annotation.Coordinates;
import com.illtamer.infinite.bot.api.event.Event;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

/**
 * 请求上报 / 通知上报 事件
 * */
@Setter
@Getter
@ToString(callSuper = true)
@Coordinates(
        postType = Coordinates.PostType.REQUEST,
        secType = "*"
)
public class RequestEvent extends Event {

    /**
     * 请求类型
     * */
    @SerializedName("request_type")
    private String requestType;

    /**
     * 同意请求
     * @param remark 添加后的好友备注
     * */
    public void approve(@Nullable String remark) {

    }

    /**
     * 拒绝请求
     * */
    public void reject() {

    }

}
