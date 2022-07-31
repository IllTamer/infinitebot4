package com.illtamer.infinite.bot.api.event.notice;

import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.annotation.Coordinates;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 精华消息事件
 * */
@Setter
@Getter
@ToString(callSuper = true)
@Coordinates(
        postType = Coordinates.PostType.NOTICE,
        secType = "essence",
        subType = {"add", "delete"}
)
public class EssenceMessageEvent extends NoticeEvent {

    /**
     * 添加为add,移出为delete
     * <p>
     * add,delete
     * */
    @SerializedName("sub_type")
    private String subType;

    /**
     * 消息发送者ID
     * */
    @SerializedName("sender_id")
    private Long senderId;

    /**
     * 操作者ID
     * */
    @SerializedName("operator_id")
    private Long operatorId;

    /**
     * 消息ID
     * */
    @SerializedName("message_id")
    private Integer messageId;

}
