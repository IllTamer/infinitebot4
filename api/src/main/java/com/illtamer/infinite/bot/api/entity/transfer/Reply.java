package com.illtamer.infinite.bot.api.entity.transfer;

import com.illtamer.infinite.bot.api.entity.TransferEntity;
import lombok.Data;

@Data
public class Reply implements TransferEntity {

    /**
     * 回复时所引用的消息id, 必须为本群消息.
     * */
    private Integer id;

    /**
     * 自定义回复的信息
     * */
    private String text;

    /**
     * 回复的QQ
     * */
    private Long qq;

    /**
     * 回复的时间
     * */
    private Long time;

    /**
     * 起始消息序号
     * */
    private Long seq;

}
