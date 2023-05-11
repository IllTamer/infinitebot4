package com.illtamer.infinite.bot.api.handler;

import com.illtamer.infinite.bot.api.Response;
import com.illtamer.infinite.bot.api.config.CQHttpWebSocketConfiguration;
import com.illtamer.infinite.bot.api.entity.*;
import com.illtamer.infinite.bot.api.entity.transfer.Text;
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
     * 删除好友
     * @param friendId 好友 QQ 号
     * */
    public static boolean deleteFriend(long friendId) {
        final Response<Map<String, Object>> response = new FriendDeleteHandler()
                .setFriendId(friendId)
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
     * 发送私人消息
     * @return 消息 ID
     * */
    public static Integer sendMessage(String message, long userId) {
        return sendMessage(MessageBuilder.json().text(message).build(), userId);
    }

    /**
     * 发送私人消息
     * @return 消息 ID
     * */
    public static Integer sendMessage(Message message, long userId) {
        return sendMessage(message, userId, 2000);
    }

    /**
     * 发送私人消息
     * @param limit 最大字符数限制
     * @return 消息 ID
     * */
    public static Integer sendMessage(Message message, long userId, int limit) {
        Message limited;
        if ((limited = buildLimitMessage(message, limit)) != null) return sendPrivateForwardMessage(limited, userId);
        Response<Map<String, Object>> response = new PrivateMsgSendHandler()
                .setUserId(userId)
                .setMessage(message)
                .request();
        return (int) ((Double) response.getData().get("message_id")).doubleValue();
    }

    /**
     * 发送临时会话消息
     * @param groupId 消息来源群组
     * @return 消息 ID
     * */
    public static Integer sendTempMessage(String message, long userId, long groupId) {
        return sendTempMessage(MessageBuilder.json().text(message).build(), userId, groupId);
    }

    /**
     * 发送临时会话消息
     * @param groupId 消息来源群组
     * @return 消息 ID
     * */
    public static Integer sendTempMessage(Message message, long userId, long groupId) {
        return sendTempMessage(message, userId, groupId, 2000);
    }

    /**
     * 发送临时会话消息
     * @param groupId 消息来源群组
     * @param limit 最大字符数限制
     * @return 消息 ID
     * */
    public static Integer sendTempMessage(Message message, long userId, long groupId, int limit) {
        Message limited;
        if ((limited = buildLimitMessage(message, limit)) != null) return sendPrivateForwardMessage(limited, userId);
        Response<Map<String, Object>> response = new PrivateMsgSendHandler()
                .setUserId(userId)
                .setGroupId(groupId)
                .setMessage(message)
                .request();
        return (int) ((Double) response.getData().get("message_id")).doubleValue();
    }

    /**
     * 发送群消息
     * @return 消息 ID
     * */
    public static Integer sendGroupMessage(String message, long groupId) {
        return sendGroupMessage(MessageBuilder.json().text(message).build(), groupId);
    }

    /**
     * 发送群消息
     * @return 消息 ID
     * */
    public static Integer sendGroupMessage(Message message, long groupId) {
        return sendGroupMessage(message, groupId, 2000);
    }

    /**
     * 发送群消息
     * @return 消息 ID
     * */
    public static Integer sendGroupMessage(Message message, long groupId, int limit) {
        Message limited;
        if ((limited = buildLimitMessage(message, limit)) != null) return sendGroupForwardMessage(limited, groupId);
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
    public static Integer sendPrivateForwardMessage(Message messageNode, long userId) {
        Response<Map<String, Object>> response = new PrivateForwardSendHandler()
                .setUserId(userId)
                .setMessages(messageNode)
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

    public static com.illtamer.infinite.bot.api.entity.Message getMessage(int messageId) {
        final Response<Map<String, Object>> response = new GetMessageHandler()
                .setMessageId(messageId)
                .request();
        return GetMessageHandler.parse(response);
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

    public static GroupMember getGroupMember(long groupId, long userId) {
        return getGroupMember(groupId, userId, true);
    }

    public static GroupMember getGroupMember(long groupId, long userId, boolean cache) {
        final Response<Map<String, Object>> response = new GroupMemberGetHandler()
                .setGroupId(groupId)
                .setUserId(userId)
                .setNoCache(!cache)
                .request();
        return GroupMemberGetHandler.parse(response);
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
     * 获取登录账号信息
     * */
    public static LoginInfo getLoginInfo() {
        final Response<Map<String, Object>> response = new GetLoginInfoHandler().request();
        return GetLoginInfoHandler.parse(response.getData());
    }

    /**
     * 获取陌生人信息
     * @param userId 用户 QQ
     * */
    public static Stranger getStranger(long userId) {
        return getStranger(userId, true);
    }

    /**
     * 获取陌生人信息
     * @param userId 用户 QQ
     * @param cache 是否使用缓存
     * */
    public static Stranger getStranger(long userId, boolean cache) {
        final Response<Map<String, Object>> response = new StrangerGetHandler()
                .setUserId(userId)
                .setNoCache(!cache)
                .request();
        return StrangerGetHandler.parse(response);
    }

    /**
     * 获取机器人状态信息
     * */
    public static BotStatus getStatus() {
        final Response<Map<String, Object>> response = new StatusGetHandler().request();
        return StatusGetHandler.parse(response);
    }

    public static Message buildLimitMessage(Message message, int limit) {
        List<TransferEntity> entities = message.getMessageChain().getEntities();
        int length = entities.stream().filter(e -> e instanceof Text).mapToInt(e -> ((Text) e).getText().length()).sum();
        if (length < limit) return null;

        LoginInfo info = CQHttpWebSocketConfiguration.getLoginInfo();
        MessageBuilder mergeNodes = MessageBuilder.json();
        MessageBuilder nodeBuilder = MessageBuilder.json();
        int nodeLen = 0;
        for (TransferEntity entity : entities) {
            // TODO 改进内容长度算法，TransferEntity 加 #length 接口
            int entityLen = entity instanceof Text ? ((Text) entity).getText().length() : 20;
            nodeLen += entityLen;
            if (nodeLen <= limit) {
                nodeBuilder.add(entity);
                continue;
            }
            // 非 text 类型不可拆分，直接并入消息
            if (!(entity instanceof Text)) {
                mergeNodes.customMessageNode(info.getNickname(), info.getUserId(), nodeBuilder.build(), null);
                mergeNodes.customMessageNode(info.getNickname(), info.getUserId(), MessageBuilder.json().add(entity).build(), null);
            } else {
                String text = ((Text) entity).getText();
                int split = limit - (nodeLen - entityLen);
                nodeBuilder.text(text.substring(0, split));
                mergeNodes.customMessageNode(info.getNickname(), info.getUserId(), nodeBuilder.build(), null);
                recursionSplitText(text.substring(split), mergeNodes, info, limit);
            }
            nodeLen = 0;
            nodeBuilder = MessageBuilder.json();
        }
        if (!nodeBuilder.empty()) {
            mergeNodes.customMessageNode(info.getNickname(), info.getUserId(), nodeBuilder.build(), null);
        }
        return mergeNodes.build();
    }

    private static void recursionSplitText(String text, MessageBuilder parent, LoginInfo info, int limit) {
        if (text.length() == 0) return;
        if (text.length() <= limit) {
            parent.customMessageNode(info.getNickname(), info.getUserId(), MessageBuilder.json().text(text).build(), null);
            return;
        }
        parent.customMessageNode(info.getNickname(), info.getUserId(), MessageBuilder.json().text(text.substring(0, limit)).build(), null);
        recursionSplitText(text.substring(limit), parent, info, limit);
    }

}
