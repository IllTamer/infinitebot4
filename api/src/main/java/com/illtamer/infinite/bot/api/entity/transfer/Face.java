package com.illtamer.infinite.bot.api.entity.transfer;

import com.illtamer.infinite.bot.api.entity.TransferEntity;
import com.illtamer.infinite.bot.api.entity.enumerate.FaceType;
import lombok.Data;

@Data
public class Face implements TransferEntity {

    /**
     * QQ 表情 ID
     * <p>
     * 见 QQ 表情 ID 表
     * */
    private Integer id;

    public FaceType getType() {
        return FaceType.getFaceType(id);
    }

}
