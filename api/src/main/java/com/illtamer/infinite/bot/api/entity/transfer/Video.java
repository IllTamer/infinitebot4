package com.illtamer.infinite.bot.api.entity.transfer;

import com.illtamer.infinite.bot.api.entity.TransferEntity;
import lombok.Data;

/**
 * 短视频
 * */
@Data
public class Video implements TransferEntity {

    /**
     * 视频地址, 支持http和file发送
     * */
    private String file;

    /**
     * 视频封面, 支持http, file和base64发送, 格式必须为jpg
     * */
    private String cover;

    /**
     * 通过网络下载视频时的线程数, 默认单线程. (在资源不支持并发时会自动处理)
     * <p>
     * 2 3
     * */
    private int c;

}
