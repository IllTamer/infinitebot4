package com.illtamer.infinite.bot.api.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

/**
 * 群信息实体类
 * */
@Data
public class Group {

    /**
     * 群号
     * */
    @SerializedName("group_id")
    private Long groupId;

    /**
     * 群名称
     * */
    @SerializedName("group_name")
    private String groupName;

    /**
     * 群备注
     * */
    @Nullable
    @SerializedName("group_memo")
    private String groupMemo;

    /**
     * 群创建时间
     * @apiNote 如果机器人尚未加入群，则此项为 0
     * */
    @SerializedName("group_create_time")
    private Integer groupCreateTime;

    /**
     * 群等级
     * @apiNote 如果机器人尚未加入群，则此项为 0
     * */
    @SerializedName("group_level")
    private Integer groupLevel;

    /**
     * 成员数
     * @apiNote 如果机器人尚未加入群，则此项为 0
     * */
    @SerializedName("member_count")
    private Integer memberCount;

    /**
     * 最大成员数（群容量）
     * @apiNote 如果机器人尚未加入群，则此项为 0
     * */
    @SerializedName("max_member_count")
    private Integer maxMemberCount;

}
