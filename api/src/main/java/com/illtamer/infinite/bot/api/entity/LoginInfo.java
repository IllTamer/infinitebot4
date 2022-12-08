package com.illtamer.infinite.bot.api.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 机器人登录信息
 * */
@Data
public class LoginInfo {

    @SerializedName("user_id")
    private Long userId;

    private String nickname;

}
