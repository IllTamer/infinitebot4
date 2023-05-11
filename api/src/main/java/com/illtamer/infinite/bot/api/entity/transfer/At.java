package com.illtamer.infinite.bot.api.entity.transfer;

import com.illtamer.infinite.bot.api.entity.TransferEntity;
import lombok.Data;

@Data
public class At implements TransferEntity {

    /**
     * @ 的 QQ 号, all 表示全体成员
     * */
    private String qq;

    /**
     * @ 的 QQ 昵称, 默认为 QQ 号
     * */
    private String name;

}
