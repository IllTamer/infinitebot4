package com.illtamer.infinite.bot.api.handler;

import com.illtamer.infinite.bot.api.Response;
import com.illtamer.infinite.bot.api.entity.BotStatus;
import com.illtamer.infinite.bot.api.entity.Group;
import com.illtamer.infinite.bot.api.exception.APIInvokeException;
import com.illtamer.infinite.bot.api.message.Message;
import com.illtamer.infinite.bot.api.message.MessageBuilder;

import java.util.List;
import java.util.Map;

public class OpenAPIHandling {

    /**
     * 撤回消息
     * @param messageId 消息 ID
     * @throws APIInvokeException 当机器人权限不足（管理员撤回群主消息）时，会抛出 RECALL_API_ERROR
     * */
    public static boolean deleteMessage(int messageId) {
        Response<Map<String, Object>> response = new DeleteMsgHandler()
                .setMessageId(messageId)
                .request();
        return "ok".equals(response.getStatus());
    }

    /**
     * 群组单人禁言
     * @param groupId 群号
     * @param userId 要禁言的 QQ 号
     * @param duration 禁言时长, 单位秒, 0 表示取消禁言
     * */
    public static boolean groupBan(long groupId, long userId, int duration) {
        Response<Map<String, Object>> response = new GroupBanAPIHandler()
                .setGroupId(groupId)
                .setUserId(userId)
                .setDuration(duration)
                .request();
        return "ok".equals(response.getStatus());
    }

    /**
     * 发送消息
     * @return 消息 ID
     * */
    public static Integer sendMessage(String message, long userId) {
        Response<Map<String, Object>> response = new PrivateMsgSendHandler()
                .setUserId(userId)
                .setMessage(MessageBuilder.json().text(message).build())
                .request();
        return (int) ((Double) response.getData().get("message_id")).doubleValue();
    }

    /**
     * 发送消息
     * @return 消息 ID
     * */
    public static Integer sendMessage(Message message, long userId) {
        Response<Map<String, Object>> response = new PrivateMsgSendHandler()
                .setUserId(userId)
                .setMessage(message)
                .request();
        return (int) ((Double) response.getData().get("message_id")).doubleValue();
    }

    /**
     * 发送群消息
     * @return 消息 ID
     * */
    public static Integer sendGroupMessage(String message, long groupId) {
        Response<Map<String, Object>> response = new GroupMsgSendHandler()
                .setGroupId(groupId)
                .setMessage(MessageBuilder.json().text(message).build())
                .request();
        return (int) ((Double) response.getData().get("message_id")).doubleValue();
    }

    /**
     * 发送群消息
     * @return 消息 ID
     * */
    public static Integer sendGroupMessage(Message message, long groupId) {
        Response<Map<String, Object>> response = new GroupMsgSendHandler()
                .setGroupId(groupId)
                .setMessage(message)
                .request();
        return (int) ((Double) response.getData().get("message_id")).doubleValue();
    }

    /**
     * 发送自定义合并消息到群
     * @param messageNode 构造的节点消息
     * */
    public static Integer sendGroupForwardMessage(Message messageNode, long groupId) {
        Response<Map<String, Object>> response = new GroupForwardSendHandler()
                .setGroupId(groupId)
                .setMessages(messageNode)
                .request();
        return (int) ((Double) response.getData().get("message_id")).doubleValue();
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
