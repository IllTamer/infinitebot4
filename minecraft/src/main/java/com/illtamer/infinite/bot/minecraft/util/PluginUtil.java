package com.illtamer.infinite.bot.minecraft.util;

import com.illtamer.infinite.bot.minecraft.Bootstrap;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PluginUtil {

    public static String parseColor(String s) {
        return s.replace('&', '§').replace("§§", "&");
    }

    public static String clearColor(String s) {
        char[] chars = s.toCharArray();
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < chars.length; ++i) {
            if (chars[i] == 167 && i + 1 <= chars.length) {
                char next = chars[i + 1];
                if (checkColor(next)) {
                    ++i;
                    continue;
                }
            }
            builder.append(chars[i]);
        }
        return builder.toString();
    }

    private static boolean checkColor(char next) {
        if (next >= '0' && next <= '9') return true;
        if ((next >= 'a' && next <= 'f') || (next >= 'A' && next <= 'F')) return true;
        if ((next >= 'k' && next <= 'o') || (next >= 'K' && next <= 'O')) return true;
        return next == 'r' || next == 'x' || next == 'R' || next == 'X';
    }

    public static class Version {

        public static final String VERSION;

        static {
            final String packageName = Bootstrap.getInstance().getServer().getClass().getPackage().getName();
            VERSION = packageName.substring(packageName.lastIndexOf('.') + 1);
        }

        /**
         * @param number 中版本号，例如 v1_12_R1 -> 12
         * */
        public static boolean upper(int number) {
            final String[] split = VERSION.split("_");
            return Integer.parseInt(split[1]) > number;
        }

    }

}
