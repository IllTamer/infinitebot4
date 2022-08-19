package com.illtamer.infinite.bot.api.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AdapterUtil {

    /**
     * 入消息转义处理
     * 字符	对应实体转义序列
     * &	&amp;
     * [	&#91;
     * ]	&#93;
     * ,	&#44;
     * */
    public static String format(String s) {
        return s.replace("&", "&amp;")
                .replace("]", "&#93;")
                .replace(",", "&#44;");
    }

}
