package com.illtamer.infinite.bot.api.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class GroupMember {

    /**
     * 群号
     * */
    @SerializedName("group_id")
    private Long groupId;

    /**
     * QQ 号
     * */
    @SerializedName("user_id")
    private Long userId;

    /**
     * 昵称
     * */
    private String nickname;

    /**
     * 群名片／备注
     * */
    private String card;

    /**
     * 性别
     * <p>
     * male 或 female 或 unknown
     * */
    private String sex;

    /**
     * 年龄
     * */
    private Integer age;

    /**
     * 地区
     * */
    private String area;

    /**
     * 加群时间戳
     * */
    @SerializedName("join_time")
    private Integer joinTime;

    /**
     * 最后发言时间戳
     * */
    @SerializedName("last_sent_time")
    private Integer lastSentTime;

    /**
     * 成员等级
     * */
    private String level;

    /**
     * 角色
     * <p>
     * owner 或 admin 或 member
     * */
    private String role;

    /**
     * 是否不良记录成员
     * */
    private Boolean unfriendly;

    /**
     * 专属头衔
     * */
    private String title;

    /**
     * 专属头衔过期时间戳
     * */
    @SerializedName("title_expire_time")
    private Long titleExpireTime;

    /**
     * 是否允许修改群名片
     * */
    @SerializedName("card_changeable")
    private Boolean cardChangeable;

    /**
     * 禁言到期时间
     * */
    @SerializedName("shut_up_timestamp")
    private Long shutUpTimestamp;

}
