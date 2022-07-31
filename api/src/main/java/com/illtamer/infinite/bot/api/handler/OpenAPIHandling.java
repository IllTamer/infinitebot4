package com.illtamer.infinite.bot.api.handler;

import com.illtamer.infinite.bot.api.Response;
import com.illtamer.infinite.bot.api.message.Message;
import com.illtamer.infinite.bot.api.message.MessageBuilder;

public class OpenAPIHandling {

    /**
     * 发送消息
     * @return 消息 ID
     * */
    public static Double sendMessage(String message, long userId) {
        Response response = new PrivateMsgSendHandler()
                .setUserId(userId)
                .setMessage(MessageBuilder.json().text(message).build())
                .request();
        return (Double) response.getData().get("message_id");
    }

    /**
     * 发送消息
     * @return 消息 ID
     * */
    public static Double sendMessage(Message message, long userId) {
        Response response = new PrivateMsgSendHandler()
                .setUserId(userId)
                .setMessage(message)
                .request();
        return (Double) response.getData().get("message_id");
    }

    /**
     * 发送群消息
     * @return 消息 ID
     * */
    public static Double sendGroupMessage(String message, long groupId) {
        Response response = new GroupMsgSendHandler()
                .setGroupId(groupId)
                .setMessage(MessageBuilder.json().text(message).build())
                .request();
        return (Double) response.getData().get("message_id");
    }

    /**
     * 发送群消息
     * @return 消息 ID
     * */
    public static Double sendGroupMessage(Message message, long groupId) {
        Response response = new GroupMsgSendHandler()
                .setGroupId(groupId)
                .setMessage(message)
                .request();
        return (Double) response.getData().get("message_id");
    }

    /**
     * 发送自定义合并消息到群
     * @param messageNode 构造的节点消息
     * */
    public static Double sendGroupForwardMessage(Message messageNode, long groupId) {
        Response response = new GroupForwardSendHandler()
                .setGroupId(groupId)
                .setMessages(messageNode)
                .request();
        return (Double) response.getData().get("message_id");
    }

    /**
     * 设置群成员名片
     * */
    public static boolean setGroupMemberCard(String cardName, long userId, long groupId) {
        Response response = new GroupSetCardHandler()
                .setGroupId(groupId)
                .setUserId(userId)
                .setCard(cardName)
                .request();
        return "ok".equals(response.getStatus());
    }

}
