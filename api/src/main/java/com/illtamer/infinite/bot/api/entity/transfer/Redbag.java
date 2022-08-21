package com.illtamer.infinite.bot.api.entity.transfer;

import com.illtamer.infinite.bot.api.entity.TransferEntity;
import lombok.Data;

/**
 * 红包
 * */
@Data
public class Redbag implements TransferEntity {

    /**
     * 祝福语/口令
     * <p>
     * 恭喜发财
     * */
    private String title;

}
