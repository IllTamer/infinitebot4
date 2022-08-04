package com.illtamer.infinite.bot.api.handler;

import com.illtamer.infinite.bot.api.Response;
import com.illtamer.infinite.bot.api.entity.BotStatus;
import com.illtamer.infinite.bot.api.entity.Group;
import com.illtamer.infinite.bot.api.message.Message;
import com.illtamer.infinite.bot.api.message.MessageBuilder;

import java.util.List;
import java.util.Map;

public class OpenAPIHandling {

    /**
     * 发送消息
     * @return 消息 ID
     * */
    public static Double sendMessage(String message, long userId) {
        Response<Map<String, Object>> response = new PrivateMsgSendHandler()
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
        Response<Map<String, Object>> response = new PrivateMsgSendHandler()
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
        Response<Map<String, Object>> response = new GroupMsgSendHandler()
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
        Response<Map<String, Object>> response = new GroupMsgSendHandler()
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
        Response<Map<String, Object>> response = new GroupForwardSendHandler()
                .setGroupId(groupId)
                .setMessages(messageNode)
                .request();
        return (Double) response.getData().get("message_id");
    }

    /**
     * 设置群成员名片
     * */
    public static boolean setGroupMemberCard(String cardName, long userId, long groupId) {
        Response<Map<String, Object>> response = new GroupSetCardHandler()
                .setGroupId(groupId)
                .setUserId(userId)
                .setCard(cardName)
                .request();
        return "ok".equals(response.getStatus());
    }

    /**
     * 获取机器人所在群组
     * */
    public static List<Group> getGroups() {
        final Response<List<?>> response = new GroupListGetHandler().request();
        return GroupListGetHandler.parse(response);
    }

    /**
     * 获取机器人所在群组
     * <p>
     * 使用缓存，数据可能未及时更新
     * */
    public static List<Group> getCacheGroups() {
        final List<Group> cacheGroups = GroupListGetHandler.getCacheGroups();
        if (cacheGroups != null) return cacheGroups;
        return getGroups();
    }

    /**
     * @param file 图片缓存文件名
     * */
    public static ImageGetHandler.Image getImage(String file) {
        final Response<Map<String, Object>> response = new ImageGetHandler()
                .setFile(file)
                .request();
        return ImageGetHandler.parse(response);
    }

    /**
     * 获取机器人状态信息
     * */
    public static BotStatus getStatus() {
        final Response<Map<String, Object>> response = new StatusGetHandler().request();
        return StatusGetHandler.parse(response);
    }

}
