package com.illtamer.infinite.bot.api.entity.transfer;

import com.illtamer.infinite.bot.api.entity.TransferEntity;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

/**
 * XML 消息
 * */
@Data
public class XML implements TransferEntity {

    /**
     * xml内容, xml中的value部分, 记得实体化处理
     * */
    private String data;

    @Nullable
    private Short resid;

}
