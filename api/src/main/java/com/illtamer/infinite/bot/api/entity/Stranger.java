package com.illtamer.infinite.bot.api.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 陌生人
 * */
@Data
public class Stranger {

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
     * qid ID身份卡
     * */
    private String qid;

    /**
     * 等级
     * */
    private Integer level;

    /**
     * QQ达人（连续登录）天数
     * */
    @SerializedName("login_days")
    private Integer loginDays;

}
