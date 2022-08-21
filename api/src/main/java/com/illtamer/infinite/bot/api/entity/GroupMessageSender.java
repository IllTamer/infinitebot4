package com.illtamer.infinite.bot.api.entity;

import com.illtamer.infinite.bot.api.handler.OpenAPIHandling;
import com.illtamer.infinite.bot.api.message.Message;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class GroupMessageSender extends MessageSender {

    /**
     * 所在群号
     * */
    private Long groupId;

    /**
     * 群名片／备注
     * */
    private String card;

    /**
     * 地区
     * */
    private String area;

    /**
     * 成员等级
     * */
    private String level;

    /**
     * 角色, owner 或 admin 或 member
     * */
    private String role;

    /**
     * 专属头衔
     * */
    private String title;

    /**
     * 向该消息发送者发送消息
     * @return 消息 ID
     * */
    public Integer sendGroupMessage(String message) {
        return OpenAPIHandling.sendGroupMessage(message, groupId);
    }

    /**
     * 向该消息发送者发送消息
     * @return 消息 ID
     * */
    public Integer sendGroupMessage(Message message) {
        return OpenAPIHandling.sendGroupMessage(message, groupId);
    }

}

