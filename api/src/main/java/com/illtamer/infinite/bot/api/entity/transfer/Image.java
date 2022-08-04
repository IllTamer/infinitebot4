package com.illtamer.infinite.bot.api.entity.transfer;

import com.illtamer.infinite.bot.api.entity.TransferEntity;
import lombok.Data;

@Data
public class Image implements TransferEntity {

    /**
     * 图片文件名
     * */
    private String file;

    /**
     * 图片类型,
     * <p>
     * - flash 表示闪照
     * - show 表示秀图, 默认普通图片
     * */
    private String type;

    /**
     * 图片子类型, 只出现在群聊.
     * */
    private String subType;

    /**
     * 图片 URL
     * */
    private String url;

    /**
     * 是否使用缓存
     * <p>
     * 只在通过网络 URL 发送时有效, 表示是否使用已缓存的文件, 默认 1
     * */
    private Integer cache;

    /**
     * 发送秀图时的特效id
     * <p>
     * 默认为40000
     * */
    private Integer id;

    /**
     * 通过网络下载图片时的线程数
     * <p>
     * 默认单线程. (在资源不支持并发时会自动处理)
     * */
    private Integer c;

}
