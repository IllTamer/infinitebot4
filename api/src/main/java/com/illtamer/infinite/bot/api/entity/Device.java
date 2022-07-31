package com.illtamer.infinite.bot.api.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 客户端实体类
 * */
@Data
public class Device {

    /**
     * 客户端ID
     * */
    @SerializedName("app_id")
    private Long appId;

    /**
     * 设备名称
     * */
    @SerializedName("device_name")
    private String deviceName;

    /**
     * 设备类型
     * */
    @SerializedName("device_kind")
    private String deviceKind;

}
