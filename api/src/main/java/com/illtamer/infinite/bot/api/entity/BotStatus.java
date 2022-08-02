package com.illtamer.infinite.bot.api.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 机器人状态实体类
 * @see com.illtamer.infinite.bot.api.handler.StatusGetHandler
 * */
@Getter
@Setter
@ToString
public class BotStatus {

    /**
     * 原 CQHTTP 字段, 恒定为 true
     * */
    @SerializedName("app_initialized")
    private Boolean appInitialized;

    /**
     * 原 CQHTTP 字段, 恒定为 true
     * */
    @SerializedName("app_enabled")
    private Boolean appEnabled;

    /**
     * 原 CQHTTP 字段, 恒定为 true
     * */
    @SerializedName("plugins_good")
    private Boolean pluginsGood;

    /**
     * 原 CQHTTP 字段, 恒定为 true
     * */
    @SerializedName("app_goods")
    private Boolean appGood;

    /**
     * 表示BOT是否在线
     * */
    private Boolean online;

    /**
     * 同 online
     * */
    private Boolean good;

    /**
     * 运行统计
     * */
    private Statistics stat;

    @Getter
    @Setter
    @ToString
    public static class Statistics {

        /**
         * 收到的数据包总数
         * */
        @SerializedName("packet_received")
        private Long packetReceived;

        /**
         * 发送的数据包总数
         * */
        @SerializedName("packet_sent")
        private Long packetSent;

        /**
         * 数据包丢失总数
         * */
        @SerializedName("packet_lost")
        private Integer packetLost;

        /**
         * 接受信息总数
         * */
        @SerializedName("message_received")
        private Long messageReceived;

        /**
         * 发送信息总数
         * */
        @SerializedName("message_sent")
        private Long messageSent;

        /**
         * TCP 链接断开次数
         * */
        @SerializedName("disconnect_times")
        private Integer disconnectTimes;

        /**
         * 账号掉线次数
         * */
        @SerializedName("lost_times")
        private Integer lostTimes;

        /**
         * 最后一条消息时间
         * */
        @SerializedName("last_message_time")
        private Long lastMessageTime;

    }

}
