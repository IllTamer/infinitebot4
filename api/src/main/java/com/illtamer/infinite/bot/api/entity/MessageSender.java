package com.illtamer.infinite.bot.api.entity;

import com.google.gson.annotations.SerializedName;
import com.illtamer.infinite.bot.api.handler.OpenAPIHandling;
import com.illtamer.infinite.bot.api.message.Message;
import lombok.Data;

@Data
public class MessageSender {

    /**
     * 发送者 QQ 号
     * */
    @SerializedName("user_id")
    private Long userId;

    /**
     * 昵称
     * */
    private String nickname;

    /**
     * 性别, male 或 female 或 unknown
     * */
    private String sex;

    /**
     * 年龄
     * */
    private Integer age;

    /**
     * 向该消息发送者发送消息
     * @return 消息 ID
     * */
    public Integer sendMessage(String message) {
        return OpenAPIHandling.sendMessage(message, userId);
    }

    /**
     * 向该消息发送者发送消息
     * @return 消息 ID
     * */
    public Integer sendMessage(Message message) {
        return OpenAPIHandling.sendMessage(message, userId);
    }

}
